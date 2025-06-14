package com.nexage.app.util.validator.rule.queryfield;

import com.nexage.app.services.sellingrule.impl.SellerRuleAPIQueryField;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator responsible for validation of query fields used in seller rule searches for API users.
 * See also {@link AbstractQueryFieldParameterValidator} class for more details.
 */
public class SellerRuleAPIQueryFieldParameterValidator
    extends AbstractQueryFieldParameterValidator<
        SellerRuleAPIQueryField,
        SellerRuleAPIQueryFieldParameterConstraint,
        SellerRuleAPIQueryParams> {

  /**
   * Constructor that creates an instance of {@link AbstractQueryFieldParameterValidator} for {@link
   * SellerRuleAPIQueryField} query fields.
   */
  public SellerRuleAPIQueryFieldParameterValidator() {
    super(SellerRuleAPIQueryField.values());
  }

  /** {@inheritDoc} */
  @Override
  public boolean isValid(SellerRuleAPIQueryParams value, ConstraintValidatorContext context) {
    return validate(value.getFields(), context);
  }
}
