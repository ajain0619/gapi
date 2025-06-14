package com.nexage.app.util.validator.site;

import com.nexage.app.services.site.SiteQueryField;
import com.nexage.app.util.validator.rule.queryfield.AbstractQueryFieldParameterValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator responsible for validation of query fields used in site searches for UI users. See also
 * {@link AbstractQueryFieldParameterValidator} class for more details.
 */
public class SiteQueryFieldParameterValidator
    extends AbstractQueryFieldParameterValidator<
        SiteQueryField, SiteQueryFieldParameterConstraint, SiteQueryParams> {

  /**
   * Constructor that creates an instance of {@link AbstractQueryFieldParameterValidator} for {@link
   * SiteQueryField} query fields.
   */
  public SiteQueryFieldParameterValidator() {
    super(SiteQueryField.values());
  }

  /** {@inheritDoc} */
  @Override
  public boolean isValid(SiteQueryParams value, ConstraintValidatorContext context) {
    return validate(value.getFields(), context);
  }
}
