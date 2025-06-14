package com.nexage.app.web.deal;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.deals.SpecificAssignedInventoryDTO;
import com.nexage.app.services.deal.DealSpecificAssignedInventoryService;
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
public class DealSpecificAssignedInventoryController {

  public static final String SPECIFIC_INVENTORY_CONTENT_TYPE =
      "application/vnd.geneva-api.assigned-inventories-specific+json";
  public static final String SPECIFIC_INVENTORY_UNPAGED_CONTENT_TYPE =
      "application/vnd.geneva-api.assigned-inventories.unpaged-specific+json";

  private final DealSpecificAssignedInventoryService specificAssignedInventoryDTOService;

  public DealSpecificAssignedInventoryController(
      DealSpecificAssignedInventoryService specificAssignedInventoryDTOService) {
    this.specificAssignedInventoryDTOService = specificAssignedInventoryDTOService;
  }

  /**
   * Update specified assigned inventory specific data.
   *
   * @param dealPid deal PID
   * @param dealAssignedInventory specific assigned inventory
   * @return 204 status with no body
   */
  @Operation(summary = "Update specific assigned inventory")
  @Timed
  @ExceptionMetered
  @PostMapping(consumes = {SPECIFIC_INVENTORY_CONTENT_TYPE})
  public ResponseEntity<SpecificAssignedInventoryDTO> createNewAssignedInventory(
      @PathVariable("dealPid") Long dealPid,
      @Valid @RequestBody SpecificAssignedInventoryDTO dealAssignedInventory) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            specificAssignedInventoryDTOService.createNewAssignedInventory(
                dealPid, dealAssignedInventory));
  }

  /**
   * Get specific assigned inventory for specified deal.
   *
   * @param dealPid deal PID
   * @return specific assigned inventory
   */
  @Operation(summary = "Get specific assigned inventory")
  @Timed
  @ExceptionMetered
  @GetMapping(produces = {SPECIFIC_INVENTORY_UNPAGED_CONTENT_TYPE})
  public ResponseEntity<SpecificAssignedInventoryDTO> getSpecificAssignedInventory(
      @PathVariable("dealPid") Long dealPid) {
    return ResponseEntity.ok(specificAssignedInventoryDTOService.getAssignedInventory(dealPid));
  }
}
