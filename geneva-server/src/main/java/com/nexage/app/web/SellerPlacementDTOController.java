package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.services.PlacementsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "/v1/sellers/{sellerId}/placements")
@RestController
@RequestMapping(
    value = "/v1/sellers/{sellerId}/placements",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerPlacementDTOController {

  private final PlacementsService placementsService;

  public SellerPlacementDTOController(PlacementsService placementsService) {
    this.placementsService = placementsService;
  }

  /**
   * GET resource to retrieve paginated {@link PlacementDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}.
   * @param placementType placementType
   * @param status status
   * @param qt The term to be found. Query term. Optional.
   * @return {@link ResponseEntity} of type {@link Page} {@link PlacementDTO}.
   */
  @Operation(summary = "Get placements for a seller")
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<PlacementDTO>> getPlacementsByQuery(
      @PageableDefault Pageable pageable,
      @PathVariable Long sellerId,
      @Parameter(name = "the term that is being searched") @RequestParam Optional<String> qt,
      @Parameter(
              name =
                  "terms to filter on, "
                      + "Allowed values are BANNER, INTERSTITIAL, "
                      + "MEDIUM_RECTANGLE, INSTREAM_VIDEO, "
                      + "REWARDED_VIDEO")
          @RequestParam
          Optional<List<String>> placementType,
      @Parameter(name = "The status, can be one of DELETED, " + "INACTIVE, ACTIVE") @RequestParam
          Optional<List<String>> status) {
    return ResponseEntity.ok(
        placementsService.getPlacements(
            pageable, Optional.empty(), sellerId, qt, placementType, status));
  }
}
