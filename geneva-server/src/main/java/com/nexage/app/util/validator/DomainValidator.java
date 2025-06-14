package com.nexage.app.util.validator;

import javax.validation.ConstraintValidatorContext;

public class DomainValidator extends BaseValidator<DomainConstraint, String> {

  @Override
  public boolean isValid(String domain, ConstraintValidatorContext context) {
    return org.apache.commons.validator.routines.DomainValidator.getInstance().isValid(domain);
  }
}
