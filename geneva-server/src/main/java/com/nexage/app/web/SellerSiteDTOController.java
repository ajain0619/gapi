package com.nexage.app.web;

import static com.ssp.geneva.common.base.annotation.ExternalAPI.WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.services.SiteService;
import com.ssp.geneva.common.base.annotation.ExternalAPI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "/v1/sellers/{sellerId}/sites")
@RequestMapping(value = "/v1/sellers/{sellerId}/sites", produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerSiteDTOController {

  private final SiteService siteService;

  public SellerSiteDTOController(SiteService siteService) {
    this.siteService = siteService;
  }

  /**
   * GET resource to retrieve paginated {@link SiteDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}.
   * @param status status
   * @param siteType siteType
   * @param qt The term that is being searched
   * @return {@link ResponseEntity} of type {@link Page} {@link SiteDTO}.
   */
  @Operation(
      summary = "Get sites for a seller",
      description =
          "Optional filtering arguments can be passed including QueryTerm, SiteTypes, status etc...")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SiteDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<Page<SiteDTO>> getSites(
      @PageableDefault(value = 10, page = 0) Pageable pageable,
      @PathVariable(value = "sellerId") long sellerId,
      @Parameter(name = "The term that is being searched") @RequestParam("qt") Optional<String> qt,
      @Parameter(
              name =
                  "The terms we want to filter on. Allowed values are MOBILE_WEB, ANDROID, IOS, DESKTOP, WEBSITE, CTV_OTT",
              content =
                  @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
          @RequestParam(value = "siteType")
          Optional<List<String>> siteType,
      @Parameter(name = "The status, can be one of DELETED, INACTIVE, ACTIVE")
          @RequestParam(name = "status")
          Optional<List<String>> status,
      @Parameter(
              name = "Fetch type 'limited' returns filtered fields. If empty, returns all fields")
          @RequestParam(name = "fetch")
          Optional<String> fetch,
      @Parameter(name = "Requests only minimal site data to be returned.")
          @RequestParam(name = "minimal", defaultValue = "false")
          Optional<Boolean> minimal) {
    if (minimal.orElse(false)) {
      return ResponseEntity.ok(siteService.getSiteMinimalData(sellerId, qt.orElse(""), pageable));
    }
    return ResponseEntity.ok(siteService.getSites(sellerId, pageable, qt, siteType, status, fetch));
  }

  @Operation(summary = "Search sites and placements")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SiteDTO.class)))
  @Timed
  @ExceptionMetered
  @RequestMapping(value = "/globalSearch", method = RequestMethod.GET)
  public ResponseEntity<Page<SiteDTO>> globalSearch(
      @PageableDefault(value = 10, page = 0) Pageable pageable,
      @PathVariable(value = "sellerId") long sellerId,
      @Parameter(name = "The term that is being searched") @RequestParam("qt")
          Optional<String> qt) {
    return ResponseEntity.ok(siteService.searchSitesAndPositionsForSeller(sellerId, qt, pageable));
  }
}
