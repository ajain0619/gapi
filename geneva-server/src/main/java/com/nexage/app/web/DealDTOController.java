package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.deals.DealDTO;
import com.nexage.app.services.DealDTOService;
import com.nexage.app.util.validator.BuyerSellerSeatDealQueryFieldParams;
import com.ssp.geneva.common.model.search.annotation.MultiValueSearchParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import javax.validation.Valid;
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

@RestController
@Tag(name = "/v1/deals", description = "This base endpoint interacts with deals")
@RequestMapping(value = "/v1/deals", produces = MediaType.APPLICATION_JSON_VALUE)
public class DealDTOController {

  private final DealDTOService dealService;

  public DealDTOController(DealDTOService dealDTOService) {
    this.dealService = dealDTOService;
  }

  @Operation(
      summary = "Get all Deals",
      description = "Optional filtering arguments can be passed including Description and Deal ID")
  @ApiResponse(content = @Content(schema = @Schema(implementation = DealDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<DealDTO>> getPagedDeals(
      @Parameter(name = "Query term for search") @RequestParam(value = "qt", required = false)
          String qt,
      @Parameter(name = "Query field for search") @RequestParam(value = "qf", required = false)
          Set<String> qf,
      @PageableDefault(value = 10) Pageable pageable) {
    return ResponseEntity.ok(dealService.findAll(qt, qf, pageable));
  }

  /**
   * Get One {@link DealDTO}.
   *
   * @param dealPid the deal pid
   * @return ResponseEntity DealDTO
   */
  @Operation(summary = "Get Deal")
  @ApiResponse(content = @Content(schema = @Schema(implementation = DealDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{dealPid}")
  public ResponseEntity<DealDTO> getDeal(@PathVariable(value = "dealPid") long dealPid) {
    return ResponseEntity.ok(dealService.findOne(dealPid));
  }

  /**
   * @param pageable pageable defatult will be 100 and sort by pid
   * @param search BuyerSellerSeatDealQueryFieldParams to search multivalueparams such as sellers,
   *     dspBuyerSeats. dealid
   * @return paginated response of the DirectDealViewDTO object
   */
  @Operation(
      summary =
          "Get Deals based on sellers, dealId, buyercompany and buyerSeats as multiValueSearchParams")
  @ApiResponse(content = @Content(schema = @Schema(implementation = DealDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping(params = {"multiSearch", "qf"})
  @Parameter(schema = @Schema(implementation = String.class), name = "qf", required = true)
  public ResponseEntity<Page<DealDTO>> getValidDeals(
      @PageableDefault(sort = "pid", size = 100) Pageable pageable,
      @MultiValueSearchParams @Valid BuyerSellerSeatDealQueryFieldParams search) {

    return ResponseEntity.ok(dealService.getDeals(search, pageable));
  }
}
