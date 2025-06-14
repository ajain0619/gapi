package com.nexage.app.web.deal;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.deals.DealBidderDTO;
import com.nexage.app.services.DealBidderDTOService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/deals/bidders")
@RestController
@RequestMapping(value = "/v1/deals/bidders", produces = MediaType.APPLICATION_JSON_VALUE)
public class DealBidderDTOController {

  final DealBidderDTOService dealBidderDTOService;

  public DealBidderDTOController(DealBidderDTOService dealBidderDTOService) {
    this.dealBidderDTOService = dealBidderDTOService;
  }

  /**
   * GET resource to retrieve paginated {@link DealBidderDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}.
   * @param qf Unique {@link Set} of fields. Query fields. Optional.
   * @param qt The term to be found. Query term. Optional.
   * @return {@link ResponseEntity} of type {@link Page} {@link DealBidderDTO}.
   */
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<DealBidderDTO>> getBidders(
      @PageableDefault Pageable pageable,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt) {
    return ResponseEntity.ok(dealBidderDTOService.findAll(qf, qt, pageable));
  }
}
