package com.nexage.app.services;

import com.nexage.app.dto.inventory.attributes.InventoryAttributeValueDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contains business logic for working with inventory attributes and their values at the seller
 * level.
 */
public interface SellerInventoryAttributeValueService {

  /**
   * Updates specified inventory attribute value.
   *
   * @param sellerPid seller PID
   * @param inventoryAttributePid inventory attribute PID
   * @param inventoryAttributeValuePid inventory attribute value PID
   * @param inventoryAttributeValueDto inventory attribute value DTO
   * @return updated value
   */
  InventoryAttributeValueDTO updateInventoryAttributeValue(
      Long sellerPid,
      Long inventoryAttributePid,
      Long inventoryAttributeValuePid,
      InventoryAttributeValueDTO inventoryAttributeValueDto);

  /**
   * Returns paged list of all inventory attribute values for a given {@code sellerPid} and {@code
   * inventoryAttributePid }
   *
   * @param sellerPid seller PID
   * @param inventoryAttributePid inventory attribute PID
   * @param pageable pageable parameter
   * @return pages list of inventory attribute values
   */
  Page<InventoryAttributeValueDTO> getAllValuesForInventoryAttribute(
      Long sellerPid, Long inventoryAttributePid, Pageable pageable);

  /**
   * Creates new inventory attribute value for a given inventory attribute and seller.
   *
   * @param sellerPid seller ID
   * @param inventoryAttributePid invntory attribute ID
   * @param inventoryAttributeValueDto inventory attribute value to create
   * @return newly created value
   */
  InventoryAttributeValueDTO createInventoryAttributeValue(
      Long sellerPid,
      Long inventoryAttributePid,
      InventoryAttributeValueDTO inventoryAttributeValueDto);
}
