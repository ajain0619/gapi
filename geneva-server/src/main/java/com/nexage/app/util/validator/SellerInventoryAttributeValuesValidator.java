package com.nexage.app.util.validator;

import com.nexage.app.dto.inventory.attributes.InventoryAttributeValueDTO;

/** Validator for seller inventory attribute operations. */
public interface SellerInventoryAttributeValuesValidator {

  /**
   * Validate parameters for updating inventory attribute value. This method will check the
   * following: - field {@code pid} in {@code inventoryAttributeValueDTO} parameter is not null -
   * parameter {@code attributeValuePid} matches field {@code pid} of parameter {@code
   * inventoryAttributeValueDTO} - attribute with PID {@code attributePid} exists - attribute value
   * with PID {@code attributeValuePid} exists
   *
   * @param attributePid attribute PID
   * @param attributeValuePid attribute value PID
   * @param inventoryAttributeValueDTO attribute value DTO
   */
  void validateForUpdate(
      Long attributePid,
      Long attributeValuePid,
      InventoryAttributeValueDTO inventoryAttributeValueDTO);

  /**
   * Validates that specified attribute belong to a specified seller.
   *
   * @param sellerPid seller PID
   * @param attributePid attribute PID
   */
  void validateForFetch(Long sellerPid, Long attributePid);

  /**
   * Validates parameters for creating new inventory attribute value. This method will check the
   * following cases:
   * <li>
   *
   *     <ul>
   *       {@code attributePid} is not null
   * </ul>
   *
   * <ul>
   *   inventory attribute with specified {@code attributePid} exists
   * </ul>
   *
   * <ul>
   *   inventory attribute belong to seller with PID {@code sellerPid}
   * </ul>
   *
   * <ul>
   *   PID is not set for {@code inventoryAttributeValueDTO}
   * </ul>
   *
   * @param sellerPid
   * @param attributePid
   * @param inventoryAttributeValueDTO
   */
  void validateForCreate(
      Long sellerPid, Long attributePid, InventoryAttributeValueDTO inventoryAttributeValueDTO);
}
