package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deals.DealDTO;
import com.nexage.app.mapper.deal.DealDTOMapper;
import com.nexage.app.services.SellerDealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

@RestController
@Tag(name = "/v1/sellers/{sellerId}/deals")
@RequestMapping(value = "/v1/sellers/{sellerId}/deals", produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerDealController {

  private final SellerDealService sellerDealService;

  public SellerDealController(SellerDealService sellerDealService) {
    this.sellerDealService = sellerDealService;
  }

  /**
   * Get all deals associated with a seller by inventory. Deals can be associated with a seller
   * either directly, by site or by position
   *
   * @param qt {@link Set} of fields to query by. May include 'dealId' or 'description' only
   * @param qf Term to use in query when qf parameter is specified
   * @param pageable {@link Pageable} to use for pagination
   * @param sellerId PID of the {@link Company}
   * @return
   */
  @Operation(
      summary =
          "Get all Deals associated with a seller by inventory. "
              + "Inventory includes sites, positions and direct associations with the publisher ",
      description = "Result can optionally be filtered by description and dealID")
  @ApiResponse(content = @Content(schema = @Schema(implementation = DealDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<DealDTO>> getPagedDealsAssociatedWithSeller(
      @Parameter(name = "Query term for search") @RequestParam(value = "qt", required = false)
          String qt,
      @Parameter(name = "Query field for search") @RequestParam(value = "qf", required = false)
          Set<String> qf,
      @PageableDefault(size = 10) Pageable pageable,
      @PathVariable Long sellerId) {

    return ResponseEntity.ok()
        .body(
            sellerDealService
                .getPagedDealsAssociatedWithSeller(sellerId, qf, qt, pageable)
                .map(DealDTOMapper.MAPPER::map));
  }

  /**
   * Get a Deal by id associated with a seller by inventory. Inventory includes sites, positions and
   * direct associations with the publisher
   *
   * @param sellerId PID of the {@link Company}
   * @param pid PID of the {@link DirectDeal}
   * @return the {@link DirectDealDTO} that is returned from the call
   */
  @Operation(
      summary =
          "Get a Deal by id associated with a seller by inventory. "
              + "Inventory includes sites, positions and direct associations with the publisher ")
  @ApiResponse(content = @Content(schema = @Schema(implementation = DirectDealDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{pid}")
  public ResponseEntity<DirectDealDTO> getDealAssociatedWithSeller(
      @PathVariable Long sellerId, @PathVariable Long pid) {
    return ResponseEntity.ok().body(sellerDealService.getDealAssociatedWithSeller(sellerId, pid));
  }

  /**
   * Create a Deal associated with a seller
   *
   * @param sellerId PID of the {@link Company}
   * @body directDealDTO custom object {@link DirectDealDTO}
   * @return the {@link DirectDealDTO} that is returned from the call
   */
  @Operation(summary = "Create a Deal associated with a seller.")
  @ApiResponse(content = @Content(schema = @Schema(implementation = DirectDealDTO.class)))
  @Timed
  @ExceptionMetered
  @PostMapping
  public DirectDealDTO createDealAssociatedWithSeller(
      @PathVariable Long sellerId, @RequestBody DirectDealDTO directDealDTO) {
    return sellerDealService.createDealAssociatedWithSeller(sellerId, directDealDTO);
  }

  /**
   * Update a Deal associated with a seller
   *
   * @param sellerId PID of the {@link Company}
   * @param pid PID of the {@link DirectDeal}
   * @body directDealDTO custom object {@link DirectDealDTO}
   * @return the {@link DirectDealDTO} that is returned from the call
   */
  @Operation(summary = "Update a Deal associated with a seller. ")
  @ApiResponse(content = @Content(schema = @Schema(implementation = DirectDealDTO.class)))
  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{pid}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public DirectDealDTO updateDealAssociatedWithSeller(
      @PathVariable Long sellerId,
      @PathVariable Long pid,
      @RequestBody DirectDealDTO directDealDTO) {
    return sellerDealService.updateDealAssociatedWithSeller(sellerId, pid, directDealDTO);
  }
}
