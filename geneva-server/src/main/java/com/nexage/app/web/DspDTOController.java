package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.dsp.DspDTO;
import com.nexage.app.dto.dsp.DspSummaryDTO;
import com.nexage.app.dto.seller.SellerSummaryDTO;
import com.nexage.app.services.DspDTOService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.text.ParseException;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "/v1/dsps")
@RestController
@RequestMapping(value = "/v1/dsps", produces = MediaType.APPLICATION_JSON_VALUE)
public class DspDTOController {

  private final DspDTOService dspDTOService;

  public DspDTOController(DspDTOService dspDTOService) {
    this.dspDTOService = dspDTOService;
  }

  /**
   * GET resource to retrieve paginated {@link DspDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}.
   * @param qf Unique {@link Set} of fields. Query fields. Optional.
   * @param qt The term to be found. Query term. Optional.
   * @return {@link ResponseEntity} of type {@link Page} {@link DspDTO}.
   */
  @Timed
  @ExceptionMetered
  @GetMapping()
  public ResponseEntity<Page<DspDTO>> findAll(
      @PageableDefault(sort = "name") Pageable pageable,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt,
      @RequestParam(value = "isRtbEnabled", required = false, defaultValue = "false")
          boolean isRtbEnabled) {
    log.debug("qf={}, qt={}, pageable={}, isRtbEnabled={}", qf, qt, pageable, isRtbEnabled);
    return ResponseEntity.ok(dspDTOService.findAll(qf, qt, pageable, isRtbEnabled));
  }

  /**
   * GET resource to retrieve paginated {@link DspSummaryDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}
   * @param qf Unique {@link Set} of fields. Query fields. Optional.
   * @param qt The term to be found. Query term. Optional.
   * @return {@link ResponseEntity} of type {@link Page} {@link SellerSummaryDTO}
   * @throws ParseException if startDate & stopDate fails on parsing.
   */
  @Timed
  @ExceptionMetered
  @GetMapping(path = "/summary")
  public ResponseEntity<Page<DspSummaryDTO>> findAllSummary(
      @PageableDefault(sort = "name") Pageable pageable,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt,
      @RequestParam(value = "isRtbEnabled", required = false, defaultValue = "false")
          boolean isRtbEnabled)
      throws ParseException {
    log.debug("qf={}, qt={}, pageable={}, isRtbEnabled={}", qf, qt, pageable, isRtbEnabled);
    return ResponseEntity.ok(dspDTOService.findAllSummary(qf, qt, pageable, isRtbEnabled));
  }
}
