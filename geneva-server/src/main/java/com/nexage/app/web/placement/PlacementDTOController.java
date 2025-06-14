package com.nexage.app.web.placement;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.PlacementsService;
import com.nexage.app.services.SellerLimitService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(
    value = "/v1/sellers/{sellerId}/sites/{siteId}/placements",
    produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "/v1/sellers/{sellerId}/sites/{siteId}/placements")
public class PlacementDTOController {

  private final PlacementsService placementsService;
  private final SellerLimitService sellerLimitService;
  private final UserContext userContext;

  public PlacementDTOController(
      PlacementsService placementsService,
      SellerLimitService sellerLimitService,
      UserContext userContext) {
    this.placementsService = placementsService;
    this.sellerLimitService = sellerLimitService;
    this.userContext = userContext;
  }

  /**
   * GET resource to retrieve paginated {@link PlacementDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}.
   * @param status status
   * @param placementType placementType
   * @param qt query term
   * @param minimal get only minimal fields
   * @return {@link ResponseEntity} of type {@link Page} {@link PlacementDTO}.
   */
  @Operation(summary = "Get placements for seller and site")
  @ApiResponse(content = @Content(schema = @Schema(implementation = PlacementDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<PlacementDTO>> getPlacements(
      @PageableDefault Pageable pageable,
      @PathVariable Long sellerId,
      @PathVariable Long siteId,
      @Parameter(
              name =
                  "terms to filter on, Allowed values "
                      + "are BANNER, INTERSTITIAL, MEDIUM_RECTANGLE, INSTREAM_VIDEO, REWARDED_VIDEO")
          @RequestParam
          Optional<List<String>> placementType,
      @Parameter(name = "The status") @RequestParam Optional<List<String>> status,
      @Parameter(name = "Requests only minimal site data to be returned")
          @RequestParam(name = "minimal", defaultValue = "false")
          Optional<Boolean> minimal,
      @Parameter(name = "Query term") @RequestParam Optional<String> qt) {
    if (minimal.orElse(false)) {
      return ResponseEntity.ok(
          placementsService.getPlacementsMinimalData(pageable, siteId, sellerId, qt.orElse("")));
    }

    return ResponseEntity.ok(
        placementsService.getPlacements(
            pageable, Optional.of(siteId), sellerId, qt, placementType, status));
  }

  /**
   * POST resource to create a {@link PlacementDTO} for the specified Site and Seller
   *
   * @param sellerId {@link long}
   * @param siteId {@link long}
   * @param placementDTO {@link PlacementDTO}
   * @return {@link PlacementDTO}
   */
  @Operation(summary = "Creates a placement for seller and site")
  @ApiResponse(content = @Content(schema = @Schema(implementation = PlacementDTO.class)))
  @Timed
  @ExceptionMetered
  @PostMapping
  public ResponseEntity<PlacementDTO> createPlacementDTO(
      @PathVariable(value = "sellerId") long sellerId,
      @PathVariable(value = "siteId") long siteId,
      @RequestBody @Validated(value = {Default.class, CreateGroup.class})
          PlacementDTO placementDTO) {
    if (!userContext.isNexageUser()
        && sellerLimitService.isLimitEnabled(sellerId)
        && !sellerLimitService.canCreatePositionsInSite(sellerId, siteId)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_LIMIT_REACHED);
    }
    if (hasSiteIdMismatch(siteId, placementDTO)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_MISMATCH);
    }
    return new ResponseEntity<>(placementsService.save(sellerId, placementDTO), HttpStatus.CREATED);
  }

  /**
   * PUT resource to update an existing {@link PlacementDTO} for the specified Site and Seller
   *
   * @param sellerId {@link long}
   * @param siteId {@link long}
   * @param placementId {@link long}
   * @param placementDTO {@link PlacementDTO}
   * @return {@link PlacementDTO}
   */
  @Operation(summary = "Updates a placement for seller and site")
  @ApiResponse(content = @Content(schema = @Schema(implementation = PlacementDTO.class)))
  @Timed
  @ExceptionMetered
  @PutMapping("/{placementId}")
  public ResponseEntity<PlacementDTO> updatePlacementDTO(
      @PathVariable(value = "sellerId") long sellerId,
      @PathVariable(value = "siteId") long siteId,
      @PathVariable(value = "placementId") long placementId,
      @RequestBody @Validated(value = {Default.class, UpdateGroup.class})
          PlacementDTO placementDTO) {
    if (hasSiteIdMismatch(siteId, placementDTO)
        || hasPlacementIdMismatch(placementId, placementDTO)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_MISMATCH);
    }
    return new ResponseEntity<>(placementsService.update(sellerId, placementDTO), HttpStatus.OK);
  }

  private boolean hasSiteIdMismatch(long siteId, PlacementDTO placementDTO) {
    return siteId != placementDTO.getSite().getPid()
        || siteId != Optional.ofNullable(placementDTO.getSitePid()).orElse(siteId);
  }

  private boolean hasPlacementIdMismatch(long placementId, PlacementDTO placementDTO) {
    return placementDTO == null
        || placementDTO.getPid() == null
        || placementDTO.getPid() != placementId;
  }
}
