package com.nexage.app.util.validator;

import static java.util.Objects.nonNull;

import com.nexage.admin.core.repository.InventoryAttributeRepository;
import com.nexage.admin.core.repository.InventoryAttributeValueRepository;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeValueDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SellerInventoryAttributeValuesValidatorImpl
    implements SellerInventoryAttributeValuesValidator {
  private final InventoryAttributeValueRepository inventoryAttributeValueRepository;
  private final InventoryAttributeRepository inventoryAttributeRepository;

  /**
   * {@inheritDoc}
   *
   * @param attributePid attribute PID
   * @param attributeValuePid attribute value PID
   * @param inventoryAttributeValueDTO attribute value DTO
   */
  @Override
  public void validateForUpdate(
      Long attributePid,
      Long attributeValuePid,
      InventoryAttributeValueDTO inventoryAttributeValueDTO) {
    if (inventoryAttributeValueDTO.getPid() == null) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_PID_IS_NULL);
    }
    if (!inventoryAttributeValueDTO.getPid().equals(attributeValuePid)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_UPDATE_PID_MISMATCH);
    }
    if (inventoryAttributeRepository.findById(attributePid).isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST);
    }
    if (inventoryAttributeValueRepository.findById(attributeValuePid).isEmpty()) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_DOES_NOT_EXIST);
    }
  }

  @Override
  public void validateForFetch(Long sellerPid, Long attributePid) {
    var attribute = inventoryAttributeRepository.findById(attributePid);
    if (attribute.isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST);
    }
    var attributeCompanyId = attribute.get().getOwnerCompany().getPid();
    if (!sellerPid.equals(attributeCompanyId)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_OWNER_PID_MISMATCH);
    }
  }

  @Override
  public void validateForCreate(
      Long sellerPid, Long attributePid, InventoryAttributeValueDTO inventoryAttributeValueDTO) {
    if (nonNull(inventoryAttributeValueDTO.getPid())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_VALUE_PID_IS_NOT_NULL);
    }
    var attribute = inventoryAttributeRepository.findById(attributePid);
    if (attribute.isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_DOESNT_EXIST);
    }
    var attributeCompanyId = attribute.get().getOwnerCompany().getPid();
    if (!sellerPid.equals(attributeCompanyId)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_OWNER_PID_MISMATCH);
    }
  }
}
