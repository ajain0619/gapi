package com.nexage.app.services.deal;

import com.nexage.app.dto.deals.SpecificAssignedInventoryDTO;
import org.springframework.web.multipart.MultipartFile;

public interface DealSpecificAssignedInventoryService {

  /**
   * Updates assigned inventory for the deal.
   *
   * @param dealPid deal PID
   * @param dealAssignedInventoryDTO assigned inventory
   * @return updated assigned inventory
   */
  SpecificAssignedInventoryDTO createNewAssignedInventory(
      Long dealPid, SpecificAssignedInventoryDTO dealAssignedInventoryDTO);

  /**
   * Gets the assigned inventory for specified deal.
   *
   * @param dealPid deal PID
   * @return assigned inventory
   */
  SpecificAssignedInventoryDTO getAssignedInventory(Long dealPid);

  /**
   * Updates assigned inventory for the deal associated with seller.
   *
   * @param sellerId Seller ID
   * @param dealPid deal PID
   * @param dealAssignedInventoryDTO assigned inventory
   * @return updated assigned inventory
   */
  SpecificAssignedInventoryDTO createNewAssignedInventoryAssociatedWithSeller(
      Long sellerId, Long dealPid, SpecificAssignedInventoryDTO dealAssignedInventoryDTO);

  /**
   * Verifies the file content exist in DB and also checks the relation between the seller, app/site
   * and placement
   *
   * @param inventoriesFile
   * @return SpecificAssignedInventoryDTO assigned inventory
   */
  SpecificAssignedInventoryDTO processBulkInventories(MultipartFile inventoriesFile);
}
