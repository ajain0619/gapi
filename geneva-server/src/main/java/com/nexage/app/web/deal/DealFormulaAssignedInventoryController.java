package com.nexage.app.web.deal;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.deals.FormulaAssignedInventoryListDTO;
import com.nexage.app.services.deal.DealFormulaAssignedInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/deals/{dealPid}/assigned-inventories")
public class DealFormulaAssignedInventoryController {

  public static final String FORMULA_INVENTORY_CONTENT_TYPE =
      "application/vnd.geneva-api.assigned-inventories-formula+json";
  public static final String FORMULA_INVENTORY_UNPAGED_CONTENT_TYPE =
      "application/vnd.geneva-api.assigned-inventories.unpaged-formula+json";

  private final DealFormulaAssignedInventoryService formulaAssignedInventoryDTOService;

  public DealFormulaAssignedInventoryController(
      DealFormulaAssignedInventoryService formulaAssignedInventoryDTOService) {
    this.formulaAssignedInventoryDTOService = formulaAssignedInventoryDTOService;
  }

  /**
   * Update formula based assigned inventory.
   *
   * @param dealPid deal PID
   * @param dealAssignedInventory inventory formula
   * @return 204 status with no response body
   */
  @Operation(summary = "Update formula based assigned inventory")
  @Timed
  @ExceptionMetered
  @PostMapping(consumes = {FORMULA_INVENTORY_CONTENT_TYPE})
  public ResponseEntity<FormulaAssignedInventoryListDTO> updateAssignedInventory(
      @PathVariable("dealPid") Long dealPid,
      @Valid @RequestBody FormulaAssignedInventoryListDTO dealAssignedInventory) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            formulaAssignedInventoryDTOService.updateAssignedInventory(
                dealPid, dealAssignedInventory.getContent().iterator().next()));
  }

  /**
   * Get formula assigned inventory for specified deal.
   *
   * @param dealPid deal PID
   * @return specific assigned inventory
   */
  @Operation(summary = "Get formula assigned inventory")
  @Timed
  @ExceptionMetered
  @GetMapping(produces = {FORMULA_INVENTORY_UNPAGED_CONTENT_TYPE})
  public ResponseEntity<FormulaAssignedInventoryListDTO> getFormulaAssignedInventory(
      @PathVariable("dealPid") Long dealPid) {
    return ResponseEntity.ok(formulaAssignedInventoryDTOService.getAssignedInventory(dealPid));
  }
}
