package com.nexage.app.util.validator.publisherattributes;

import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.BaseValidator;
import com.ssp.geneva.common.error.handler.MessageHandler;
import javax.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PublisherAttributesValidator
    extends BaseValidator<PublisherAttributesConstraint, PublisherAttributes> {

  private final MessageHandler messageHandler;

  @Override
  public boolean isValid(
      PublisherAttributes publisherAttributes,
      ConstraintValidatorContext constraintValidatorContext) {

    Boolean hbThrottleEnabled = publisherAttributes.getHbThrottleEnabled();
    Boolean smartQPSEnabled = publisherAttributes.getSmartQPSEnabled();

    if (Boolean.TRUE.equals(hbThrottleEnabled) && Boolean.TRUE.equals(smartQPSEnabled)) {
      addConstraintMessage(
          constraintValidatorContext,
          "",
          messageHandler.getMessage(
              ServerErrorCodes.SERVER_VALIDATION_MUTUALLY_EXCLUSIVE.toString()));
      return false;
    }

    return true;
  }
}
