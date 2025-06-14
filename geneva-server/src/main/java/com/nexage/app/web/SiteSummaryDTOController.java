package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.seller.SitesSummaryDTO;
import com.nexage.app.services.SitesSummaryDTOService;
import com.nexage.dw.geneva.util.ISO8601Util;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "/v1/sellers/{sellerId}/sites/summaries")
@RequestMapping(
    value = "/v1/sellers/{sellerId}/sites/summaries",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class SiteSummaryDTOController {

  private final SitesSummaryDTOService sitesSummaryDTOService;

  public SiteSummaryDTOController(SitesSummaryDTOService sitesSummaryDTOService) {
    this.sitesSummaryDTOService = sitesSummaryDTOService;
  }

  /**
   * GET resource to retrieve paginated {@link SitesSummaryDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}
   * @param startDate start period date. ISO format: yyyy-MM-dd'T'HH:mm:ssXXX. Required.
   * @param stopDate stop period date. ISO format: yyyy-MM-dd'T'HH:mm:ssXXX. Required.
   * @param pids List of pids. Required.
   * @return {@link ResponseEntity} of type {@link SitesSummaryDTO}
   * @throws ParseException if startDate & stopDate fails on parsing.
   */
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<SitesSummaryDTO> getSitesSummary(
      @PageableDefault(value = 10, page = 0, sort = "pid", direction = Sort.Direction.ASC)
          Pageable pageable,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String startDate,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String stopDate,
      @PathVariable(value = "sellerId") long sellerId,
      @Parameter(name = "Site name") @RequestParam("name") Optional<String> name,
      @Parameter(name = "Site ids") @RequestParam(name = "pids") Optional<List<Long>> pids) {
    try {
      return ResponseEntity.ok(
          sitesSummaryDTOService.getSitesSummaryDTO(
              sellerId,
              ISO8601Util.parse(startDate),
              ISO8601Util.parse(stopDate),
              name,
              pids,
              pageable));
    } catch (DateTimeParseException | ParseException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}
