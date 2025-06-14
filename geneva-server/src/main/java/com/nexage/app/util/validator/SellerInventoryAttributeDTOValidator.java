package com.nexage.app.util.validator;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.InventoryAttributeRepository;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.LoginUserContext;
import com.ssp.geneva.common.error.handler.MessageHandler;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class SellerInventoryAttributeDTOValidator
    extends BaseValidator<SellerInventoryAttributeDTOConstraint, InventoryAttributeDTO> {

  @Autowired private InventoryAttributeRepository inventoryAttributeRepository;
  @Autowired private CompanyRepository companyRepository;
  @Autowired private LoginUserContext userContext;
  @Autowired private MessageHandler messageHandler;

  @Override
  public boolean isValid(
      InventoryAttributeDTO inventoryAttributeDTO,
      ConstraintValidatorContext constraintValidatorContext) {

    requireNonNull(inventoryAttributeDTO);
    if (isNull(inventoryAttributeDTO.getSellerPid())) {
      return addConstraintMessage(
          constraintValidatorContext,
          "sellerPid",
          ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_PUBLISHER_PID_IS_NULL);
    }
    var company = companyRepository.findById(inventoryAttributeDTO.getSellerPid());
    if (company.isEmpty()) {
      return addConstraintMessage(
          constraintValidatorContext, "sellerPid", ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }
    var exists =
        inventoryAttributeRepository.existsByNameAndPrefix(
            inventoryAttributeDTO.getName(), inventoryAttributeDTO.getPrefix());
    if (exists) {
      return addConstraintMessage(
          constraintValidatorContext,
          "name",
          ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_ATTRIBUTE_ALREADY_EXIST_FOR_THIS_PUBLISHER);
    }
    if (!inventoryAttributeDTO.getCreatedBy().equals(inventoryAttributeDTO.getSellerPid())) {
      return addConstraintMessage(
          constraintValidatorContext,
          "createdBy",
          ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_PUBLISHER_PIDS_ARENT_SAME);
    }

    if (!userContext.isNexageAdminOrManager()) {
      if (inventoryAttributeDTO.isHasGlobalVisibility()) {
        return addConstraintMessage(
            constraintValidatorContext,
            "hasGlobalVisibility",
            ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_ILLEGAL_VALUE_OF_GLOBAL_VISIBILITY);
      }
      if (inventoryAttributeDTO.isInternalOnly()) {
        return addConstraintMessage(
            constraintValidatorContext,
            "internalOnly",
            ServerErrorCodes.SERVER_INVENTORY_ATTRIBUTE_ILLEGAL_VALUE_OF_INTERNAL_ONLY);
      }
    }
    return true;
  }

  private boolean addConstraintMessage(
      ConstraintValidatorContext context, String field, ServerErrorCodes errorMessage) {
    super.addConstraintMessage(context, field, messageHandler.getMessage(errorMessage.name()));
    return false;
  }
}
