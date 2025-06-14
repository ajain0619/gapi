package com.nexage.app.util.validator.rule.queryfield;

import com.nexage.app.services.sellingrule.impl.SellerRuleQueryField;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator responsible for validation of query fields used in seller rule searches for UI users.
 * See also {@link AbstractQueryFieldParameterValidator} class for more details.
 */
public class SellerRuleQueryFieldParameterValidator
    extends AbstractQueryFieldParameterValidator<
        SellerRuleQueryField, SellerRuleQueryFieldParameterConstraint, SellerRuleQueryParams> {

  /**
   * Constructor that creates an instance of {@link AbstractQueryFieldParameterValidator} for {@link
   * SellerRuleQueryField} query fields.
   */
  public SellerRuleQueryFieldParameterValidator() {
    super(SellerRuleQueryField.values());
  }

  /** {@inheritDoc} */
  @Override
  public boolean isValid(SellerRuleQueryParams value, ConstraintValidatorContext context) {
    return validate(value.getFields(), context);
  }
}
