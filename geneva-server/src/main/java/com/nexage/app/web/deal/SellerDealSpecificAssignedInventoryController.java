package com.nexage.app.web.deal;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.deals.SpecificAssignedInventoryDTO;
import com.nexage.app.services.deal.DealSpecificAssignedInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/sellers/{sellerId}/deals/{dealPid}/assigned-inventories")
public class SellerDealSpecificAssignedInventoryController {

  private final DealSpecificAssignedInventoryService dealSpecificAssignedInventoryService;

  public SellerDealSpecificAssignedInventoryController(
      DealSpecificAssignedInventoryService dealSpecificAssignedInventoryService) {
    this.dealSpecificAssignedInventoryService = dealSpecificAssignedInventoryService;
  }

  public static final String SPECIFIC_INVENTORY_CONTENT_TYPE =
      "application/vnd.geneva-api.assigned-inventories-specific+json";

  /**
   * Update specified assigned inventory specific data associated with seller.
   *
   * @param dealPid deal PID
   * @param dealAssignedInventory specific assigned inventory
   * @return 204 status with no body
   */
  @Operation(summary = "Update specific assigned inventory associated with seller")
  @Timed
  @ExceptionMetered
  @PostMapping(consumes = {SPECIFIC_INVENTORY_CONTENT_TYPE})
  public ResponseEntity<SpecificAssignedInventoryDTO> createNewAssignedInventory(
      @PathVariable("sellerId") Long sellerId,
      @PathVariable("dealPid") Long dealPid,
      @Valid @RequestBody SpecificAssignedInventoryDTO dealAssignedInventory) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            dealSpecificAssignedInventoryService.createNewAssignedInventoryAssociatedWithSeller(
                sellerId, dealPid, dealAssignedInventory));
  }
}
