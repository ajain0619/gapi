package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.seller.SellerDTO;
import com.nexage.app.services.SellerDTOService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
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
@Tag(name = "/v1/sellers")
@RestController
@RequestMapping(value = "/v1/sellers", produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerDTOController {

  private final SellerDTOService sellerDTOService;

  public SellerDTOController(SellerDTOService sellerDTOService) {
    this.sellerDTOService = sellerDTOService;
  }

  /**
   * GET resource to retrieve paginated {@link SellerDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}.
   * @param qf Unique {@link Set} of fields. Query fields. Optional.
   * @param qt The term to be found. Query term. Optional.
   * @return {@link ResponseEntity} of type {@link Page} {@link SellerDTO}.
   */
  @Timed
  @ExceptionMetered
  @GetMapping()
  public ResponseEntity<Page<SellerDTO>> findAll(
      @PageableDefault(sort = "pid") Pageable pageable,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt,
      @RequestParam(value = "isRtbEnabled", required = false, defaultValue = "false")
          boolean isRtbEnabled) {
    log.debug("qf={}, qt={}, pageable={}", qf, qt, pageable);
    return ResponseEntity.ok(sellerDTOService.findAll(qf, qt, isRtbEnabled, pageable));
  }

  /**
   * GET resource to retrieve a single {@link SellerDTO} based on request.
   *
   * @return {@link ResponseEntity} of type {@link SellerDTO}.
   */
  @Operation(summary = "Get information for a seller")
  @Timed
  @ExceptionMetered
  @GetMapping("/{sellerPid}")
  public ResponseEntity<SellerDTO> findOne(@PathVariable Long sellerPid) {
    return ResponseEntity.ok(sellerDTOService.findOne(sellerPid));
  }
}
