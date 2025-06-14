package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.BidderConfigDTO;
import com.nexage.app.dto.BidderConfigDTOView;
import com.nexage.app.services.BidderConfigDTOService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Defines endpoints for interacting with {@link BidderConfigDTO} resources. */
@Tag(name = "/v1/dsps/{dspPid}/bidder-configs")
@RestController
@RequestMapping(
    value = "/v1/dsps/{dspPid}/bidder-configs",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class BidderConfigDTOController {

  private final BidderConfigDTOService bidderConfigDTOService;

  public BidderConfigDTOController(BidderConfigDTOService bidderConfigDTOService) {
    this.bidderConfigDTOService = bidderConfigDTOService;
  }

  /**
   * Create {@link BidderConfigDTO}.
   *
   * @param dspPid pid of the dsp the created bidder config will belong to
   * @param bidderConfigDTO create payload
   * @return created {@link BidderConfigDTO}
   */
  @Operation(description = "create bidder config")
  @Timed
  @ExceptionMetered
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BidderConfigDTO> create(
      @PathVariable Long dspPid,
      @RequestBody(required = false) @Valid BidderConfigDTO bidderConfigDTO) {
    BidderConfigDTO created = bidderConfigDTOService.create(dspPid, bidderConfigDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  /**
   * Get {@link BidderConfigDTO} by pid.
   *
   * @param dspPid pid of dsp the bidder config belongs to
   * @param bidderConfigPid pid of bidder config to find
   * @return found {@link BidderConfigDTO}
   */
  @Operation(summary = "get bidder config")
  @Timed
  @ExceptionMetered
  @GetMapping("/{bidderConfigPid}")
  public ResponseEntity<BidderConfigDTO> get(
      @PathVariable Long dspPid, @PathVariable Long bidderConfigPid) {
    BidderConfigDTO bidderConfigDTO = bidderConfigDTOService.get(dspPid, bidderConfigPid);
    return ResponseEntity.ok(bidderConfigDTO);
  }

  /**
   * Update {@link BidderConfigDTO}.
   *
   * @param dspPid pid of the dsp the bidder config to update belongs to
   * @param bidderConfigPid pid of the bidder config to update
   * @param bidderConfigDTO update payload
   * @return updated {@link BidderConfigDTO}
   */
  @Operation(summary = "update bidder config")
  @Timed
  @ExceptionMetered
  @PutMapping(value = "/{bidderConfigPid}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BidderConfigDTO> update(
      @PathVariable Long dspPid,
      @PathVariable Long bidderConfigPid,
      @RequestBody @Valid BidderConfigDTO bidderConfigDTO) {
    BidderConfigDTO updated =
        bidderConfigDTOService.update(dspPid, bidderConfigPid, bidderConfigDTO);
    return ResponseEntity.ok(updated);
  }

  /**
   * GET resource to retrieve paginated {@link BidderConfigDTOView} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}.
   * @param qf Unique {@link Set} of fields. Query fields. Optional.
   * @param qt The term to be found. Query term. Optional.
   * @return {@link ResponseEntity} of type {@link Page} {@link BidderConfigDTOView}.
   */
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<BidderConfigDTOView>> findAllSummaries(
      @PathVariable(value = "dspPid") @NotNull Long dspPid,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt,
      @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
    return ResponseEntity.ok(bidderConfigDTOService.findAllBidderConfigs(dspPid, qf, qt, pageable));
  }
}
