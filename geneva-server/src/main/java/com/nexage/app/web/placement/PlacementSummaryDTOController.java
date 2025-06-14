package com.nexage.app.web.placement;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.seller.PlacementSummaryDTO;
import com.nexage.app.services.PlacementSummaryDTOService;
import com.nexage.dw.geneva.util.ISO8601Util;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.text.ParseException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Retrieve Placements for a given seller")
@RequestMapping(value = "/v1/sellers/{sellerId}", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlacementSummaryDTOController {

  private final PlacementSummaryDTOService placementSummaryDTOService;

  public PlacementSummaryDTOController(PlacementSummaryDTOService placementSummaryDTOService) {
    this.placementSummaryDTOService = placementSummaryDTOService;
  }

  /**
   * GET resource to retrieve paginated {@link PlacementSummaryDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}
   * @param startDate start period date. ISO format: yyyy-MM-dd'T'HH:mm:ssXXX. Required.
   * @param stopDate stop period date. ISO format: yyyy-MM-dd'T'HH:mm:ssXXX. Required.
   * @param pid Placement pid
   * @param name placement name
   * @return {@link ResponseEntity} of type {@link Page} {@link PlacementSummaryDTO}
   * @throws ParseException if startDate & stopDate fails on parsing.
   */
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/sites/{siteId}/placements/summaries")
  public ResponseEntity<Page<PlacementSummaryDTO>> getPlacements(
      @PageableDefault Pageable pageable,
      @PathVariable Long sellerId,
      @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @DateTimeFormat(iso = ISO.DATE_TIME) String stopDate,
      @PathVariable Long siteId,
      @Parameter(name = "Placement pid") @RequestParam(required = false) Optional<Long> pid,
      @Parameter(name = "Placement name") @RequestParam(required = false) Optional<String> name) {
    try {
      return ResponseEntity.ok(
          placementSummaryDTOService.getPlacementsWithMetrics(
              ISO8601Util.parse(startDate),
              ISO8601Util.parse(stopDate),
              siteId,
              sellerId,
              name,
              pid,
              pageable));
    } catch (ParseException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * GET resource to retrieve paginated {@link PlacementSummaryDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}
   * @param startDate start period date. ISO format: yyyy-MM-dd'T'HH:mm:ssXXX. Required.
   * @param stopDate stop period date. ISO format: yyyy-MM-dd'T'HH:mm:ssXXX. Required.
   * @param sellerId seller pid
   * @param name placement name
   * @return {@link ResponseEntity} of type {@link Page} {@link PlacementSummaryDTO}
   * @throws ParseException if startDate & stopDate fails on parsing.
   */
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/placements/summaries")
  public ResponseEntity<Page<PlacementSummaryDTO>> getPlacementsSummaryWithoutSitePid(
      @PageableDefault Pageable pageable,
      @PathVariable Long sellerId,
      @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @DateTimeFormat(iso = ISO.DATE_TIME) String stopDate,
      @Parameter(name = "Placement name") @RequestParam(required = false) String name) {
    try {
      return ResponseEntity.ok(
          placementSummaryDTOService.getPlacementsWithMetricsWithoutSitePid(
              ISO8601Util.parse(startDate), ISO8601Util.parse(stopDate), sellerId, name, pageable));
    } catch (ParseException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}
