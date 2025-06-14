package com.nexage.app.util.validator;

import com.nexage.app.util.BuyerSellerSeatDealQueryFieldParameter;
import com.nexage.app.util.validator.rule.queryfield.AbstractQueryFieldParameterValidator;
import java.util.Arrays;
import javax.validation.ConstraintValidatorContext;

public class BuyerSellerSeatDealQueryParameterValidator
    extends AbstractQueryFieldParameterValidator<
        BuyerSellerSeatDealQueryFieldParameter,
        BuyerSellerSeatDealQueryFieldParameterConstraint,
        BuyerSellerSeatDealQueryFieldParams> {

  public BuyerSellerSeatDealQueryParameterValidator() {
    super(BuyerSellerSeatDealQueryFieldParameter.values());
  }

  /**
   * @param params BuyerSellerSeatDealQueryFieldParams object
   * @param context ConstraintValidatorContext object
   * @return true/false based on values validated for non empty and invalid keys of
   *     MultiValueSearchParams
   */
  @Override
  public boolean isValid(
      BuyerSellerSeatDealQueryFieldParams params, ConstraintValidatorContext context) {
    var fields = params.getFields();
    if (fields == null || fields.isEmpty()) {
      return false;
    }

    if (validate(fields, context)) {
      return Arrays.stream(BuyerSellerSeatDealQueryFieldParameter.values())
              .map(BuyerSellerSeatDealQueryFieldParameter::getName)
              .anyMatch(fields::containsKey)
          && validateAllFieldsHaveValues(params, BuyerSellerSeatDealQueryFieldParameter.values());
    } else {
      return false;
    }
  }
}
