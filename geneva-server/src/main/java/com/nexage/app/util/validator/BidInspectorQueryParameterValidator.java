package com.nexage.app.util.validator;

import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.BidInspectorQueryFieldParameter;
import com.nexage.app.util.validator.rule.queryfield.AbstractQueryFieldParameterValidator;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.error.model.ErrorCode;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidatorContext;

public class BidInspectorQueryParameterValidator
    extends AbstractQueryFieldParameterValidator<
        BidInspectorQueryFieldParameter,
        BidInspectorQueryFieldParameterConstraint,
        BidInspectorQueryFieldParams> {

  private final MessageHandler messageHandler;

  public BidInspectorQueryParameterValidator(MessageHandler messageHandler) {
    super(BidInspectorQueryFieldParameter.values());
    this.messageHandler = messageHandler;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isValid(BidInspectorQueryFieldParams params, ConstraintValidatorContext context) {

    var fields = params.getFields();
    if (validate(fields, context)
        && Arrays.stream(BidInspectorQueryFieldParameter.values())
            .map(BidInspectorQueryFieldParameter::getName)
            .collect(Collectors.toSet())
            .containsAll(fields.keySet())
        && validateAllFieldsHaveValues(params, BidInspectorQueryFieldParameter.values())) {
      return true;
    }
    return addConstraintMessage(
        context, getAnnotation().field(), ServerErrorCodes.SERVER_INVALID_QUERY_FIELD_PARAMETER);
  }

  boolean addConstraintMessage(
      ConstraintValidatorContext context, String field, ErrorCode errorCode) {
    super.addConstraintMessage(context, field, messageHandler.getMessage(errorCode.toString()));
    return false;
  }
}
