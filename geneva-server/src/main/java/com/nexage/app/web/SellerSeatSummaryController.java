package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.seller.SellerSummaryDTO;
import com.nexage.app.services.SellerSeatSummaryService;
import com.ssp.geneva.server.report.report.util.ISO8601Util;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.text.ParseException;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "/v1/seller-seats/{sellerSeatPid}/summaries")
@RestController
@RequestMapping(
    value = "/v1/seller-seats/{sellerSeatPid}/summaries",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerSeatSummaryController {

  private final SellerSeatSummaryService summaryDTOService;

  public SellerSeatSummaryController(SellerSeatSummaryService summaryDTOService) {
    this.summaryDTOService = summaryDTOService;
  }

  /**
   * GET resource to retrieve paginated {@link SellerSummaryDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}
   * @param startDate start period date. ISO format: yyyy-MM-dd'T'HH:mm:ssXXX. Required.
   * @param stopDate stop period date. ISO format: yyyy-MM-dd'T'HH:mm:ssXXX. Required.
   * @param qf Unique {@link Set} of fields. Query fields. Optional.
   * @param qt The term to be found. Query term. Optional.
   * @return {@link ResponseEntity} of type {@link Page} {@link SellerSummaryDTO}
   * @throws ParseException if startDate & stopDate fails on parsing.
   */
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<SellerSummaryDTO>> findSummary(
      @PathVariable Long sellerSeatPid,
      @PageableDefault
          @SortDefault.SortDefaults({
            @SortDefault(sort = "totalRevenue", direction = Sort.Direction.DESC),
            @SortDefault(sort = "pid", direction = Sort.Direction.ASC)
          })
          Pageable pageable,
      @RequestParam("startDate") @NotNull String startDate,
      @RequestParam("stopDate") @NotNull String stopDate,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt)
      throws ParseException {
    log.debug(
        "sellerSeat={}, startDate={}, stopDate={}, qf={}, qt={}, pageable={}",
        sellerSeatPid,
        startDate,
        stopDate,
        qf,
        qt,
        pageable);
    return ResponseEntity.ok(
        summaryDTOService.findSummary(
            sellerSeatPid,
            ISO8601Util.parse(startDate),
            ISO8601Util.parse(stopDate),
            qf,
            qt,
            pageable));
  }
}
