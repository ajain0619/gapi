package com.nexage.app.services.deal;

import com.nexage.app.dto.deals.FormulaAssignedInventoryDTO;
import com.nexage.app.dto.deals.FormulaAssignedInventoryListDTO;

public interface DealFormulaAssignedInventoryService {

  /**
   * Updates assigned inventory for the deal.
   *
   * @param dealPid deal PID
   * @param dealAssignedInventoryDTO assigned inventory
   * @return updated assigned inventory
   */
  FormulaAssignedInventoryListDTO updateAssignedInventory(
      Long dealPid, FormulaAssignedInventoryDTO dealAssignedInventoryDTO);

  /**
   * Gets the formula assigned inventory for specified deal.
   *
   * @param dealPid deal PID
   * @return assigned inventory
   */
  FormulaAssignedInventoryListDTO getAssignedInventory(Long dealPid);
  /**
   * Updates assigned inventory for the deal.
   *
   * @param dealPid deal PID
   * @param dealAssignedInventoryDTO assigned inventory
   * @return update seller formula
   */
  FormulaAssignedInventoryListDTO updateAssignedInventoryForSeller(
      Long sellerPid, Long dealPid, FormulaAssignedInventoryListDTO dealAssignedInventoryDTO);
}
