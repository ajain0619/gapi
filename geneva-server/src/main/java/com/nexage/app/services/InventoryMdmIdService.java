package com.nexage.app.services;

import com.nexage.app.dto.InventoryMdmIdDTO;
import com.nexage.app.util.validator.InventoryMdmIdQueryFieldParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryMdmIdService {

  /**
   * Gets the company and seller seat MDM ID lists for the current user.
   *
   * @return the {@link InventoryMdmIdDTO containing the MDM ID lists}
   */
  InventoryMdmIdDTO getMdmIdsForCurrentUser();

  /**
   * Gets the company and seller seat MDM ID lists for a seller or each seller in a deal's assigned
   * inventory.
   *
   * @param queryParams sellerPid and dealPid query parameters
   * @param pageable the {@link Pageable} parameters
   * @return a {@link Page} containing {@link InventoryMdmIdDTO}s
   */
  Page<InventoryMdmIdDTO> getMdmIdsForAssignedSellers(
      InventoryMdmIdQueryFieldParams queryParams, Pageable pageable);
}
