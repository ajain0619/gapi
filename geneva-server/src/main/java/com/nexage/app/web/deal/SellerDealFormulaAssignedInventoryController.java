package com.nexage.app.web.deal;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.deals.FormulaAssignedInventoryListDTO;
import com.nexage.app.services.deal.DealFormulaAssignedInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "/v1/sellers/{sellerPid}/deals/{dealPid}/assigned-inventories")
@RequestMapping(value = "/v1/sellers/{sellerPid}/deals/{dealPid}/assigned-inventories")
public class SellerDealFormulaAssignedInventoryController {

  public static final String FORMULA_INVENTORY_CONTENT_TYPE =
      "application/vnd.geneva-api.assigned-inventories-formula+json";

  private final DealFormulaAssignedInventoryService dealFormulaAssignedInventoryService;

  public SellerDealFormulaAssignedInventoryController(
      DealFormulaAssignedInventoryService dealFormulaAssignedInventoryService) {
    this.dealFormulaAssignedInventoryService = dealFormulaAssignedInventoryService;
  }

  /**
   * Update formula based assigned inventory.
   *
   * @param dealPid deal PID
   * @param sellerPid seller ID
   * @param dealAssignedInventory inventory formula
   * @return 200 status with response body same as request payload
   */
  @Operation(summary = "Update formula based assigned inventory")
  @Timed
  @ExceptionMetered
  @PutMapping(consumes = {FORMULA_INVENTORY_CONTENT_TYPE})
  public ResponseEntity<FormulaAssignedInventoryListDTO> updateAssignedInventoryForSeller(
      @PathVariable("sellerPid") Long sellerPid,
      @PathVariable("dealPid") Long dealPid,
      @Valid @RequestBody FormulaAssignedInventoryListDTO dealAssignedInventory) {
    return ResponseEntity.ok(
        dealFormulaAssignedInventoryService.updateAssignedInventoryForSeller(
            sellerPid, dealPid, dealAssignedInventory));
  }
}
