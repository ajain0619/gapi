package com.nexage.app.util.validator.placement;

import com.nexage.app.util.validator.BaseValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/** Validation logic for name field of a {@link com.nexage.app.dto.seller.PlacementDTO} */
public class PlacementNameValidator extends BaseValidator<PlacementNameConstraint, String> {

  private static final String POSITION_NAME_PATTERN = "^[a-z0-9_]+$";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return StringUtils.isEmpty(value) || value.matches(POSITION_NAME_PATTERN);
  }
}
