package com.nexage.app.util.validator;

import com.nexage.app.dto.CreativeRegistrationDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.handler.MessageHandler;
import javax.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

@AllArgsConstructor
public class CreativeRegistrationDTOValidator
    extends BaseValidator<CreativeRegistrationConstraint, CreativeRegistrationDTO> {

  private final MessageHandler messageHandler;

  public CreativeRegistrationDTOValidator() {
    this.messageHandler = null;
  }

  @Override
  public boolean isValid(
      CreativeRegistrationDTO dto, ConstraintValidatorContext constraintValidatorContext) {
    if (CollectionUtils.isEmpty(dto.getSellerIds()) && CollectionUtils.isEmpty(dto.getDealIds())) {
      addConstraintMessage(
          constraintValidatorContext,
          "sellerIds",
          ServerErrorCodes.SERVER_VALIDATION_INVALID_SELLER_DEAL_IDS);
      addConstraintMessage(
          constraintValidatorContext,
          "dealIds",
          ServerErrorCodes.SERVER_VALIDATION_INVALID_SELLER_DEAL_IDS);
      return false;
    } else {
      return true;
    }
  }

  private void addConstraintMessage(
      ConstraintValidatorContext constraintValidatorContext,
      String field,
      ServerErrorCodes errorCode) {
    ValidationUtils.addConstraintMessage(
        constraintValidatorContext, field, messageHandler.getMessage(errorCode.toString()));
  }
}
