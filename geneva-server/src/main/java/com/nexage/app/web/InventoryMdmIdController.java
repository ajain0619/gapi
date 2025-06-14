package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.InventoryMdmIdDTO;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.InventoryMdmIdService;
import com.nexage.app.util.validator.InventoryMdmIdQueryFieldParams;
import com.ssp.geneva.common.model.search.annotation.MultiValueSearchParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/mdmids")
@RestController
@RequestMapping(value = "/v1/mdmids", produces = MediaType.APPLICATION_JSON_VALUE)
public class InventoryMdmIdController {

  private static final String CURRENT_USER_QF = "status";
  private static final String CURRENT_USER_QT = "current";

  private final BeanValidationService beanValidationService;
  private final InventoryMdmIdService mdmIdService;

  public InventoryMdmIdController(
      BeanValidationService beanValidationService, InventoryMdmIdService mdmIdService) {
    this.beanValidationService = beanValidationService;
    this.mdmIdService = mdmIdService;
  }

  /**
   * Gets the MDM IDs for the current user or the sellers on the assigned inventory of a deal.
   *
   * @return {@link ResponseEntity} containing {@link Page}d {@link InventoryMdmIdDTO}s
   */
  @Operation(
      summary = "Gets the MDM IDs for the current user or the sellers of a deal",
      description =
          "Filtering arguments can be passed as qf=status,qt=current or qf={sellerPid=<sellerPid>,dealPid=<dealPid>}")
  @ApiResponse(content = @Content(schema = @Schema(implementation = InventoryMdmIdDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping(params = "qf")
  public ResponseEntity<Page<InventoryMdmIdDTO>> getMdmIdsForAssignedSellers(
      @PageableDefault(value = 50) Pageable pageable,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt,
      @MultiValueSearchParams InventoryMdmIdQueryFieldParams queryParams) {

    if (isRequestForCurrentUser(qt, qf)) {
      return ResponseEntity.ok(
          new PageImpl<>(
              Collections.singletonList(mdmIdService.getMdmIdsForCurrentUser()),
              Pageable.unpaged(),
              1));
    }

    beanValidationService.validate(queryParams);
    return ResponseEntity.ok(mdmIdService.getMdmIdsForAssignedSellers(queryParams, pageable));
  }

  private boolean isRequestForCurrentUser(String qt, Set<String> qf) {
    return StringUtils.equalsIgnoreCase(qt, CURRENT_USER_QT)
        && qf != null
        && qf.contains(CURRENT_USER_QF);
  }
}
