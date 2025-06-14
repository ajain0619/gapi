package com.nexage.app.services;

import com.nexage.app.dto.inventory.attributes.InventoryAttributeDTO;

public interface SellerInventoryAttributeDTOService {

  /**
   * Creates new inventory attribute for the specified seller.
   *
   * @param inventoryAttributeDTO inventory attribute to create
   * @return newly created object
   */
  InventoryAttributeDTO createInventoryAttribute(InventoryAttributeDTO inventoryAttributeDTO);

  /**
   * Fetches single inventory attribute.
   *
   * @param sellerPid seller PID
   * @param attributePid attribute PID
   * @return inventory attribute
   */
  InventoryAttributeDTO getInventoryAttribute(Long sellerPid, Long attributePid);

  /**
   * Updates specified attribute to new values.
   *
   * @param sellerPid seller ID
   * @param inventoryAttributeDTO new value object
   * @return updated object
   */
  InventoryAttributeDTO updateInventoryAttribute(
      Long sellerPid, InventoryAttributeDTO inventoryAttributeDTO);
}
