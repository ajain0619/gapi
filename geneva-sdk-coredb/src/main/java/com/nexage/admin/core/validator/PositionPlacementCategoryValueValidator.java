package com.nexage.admin.core.validator;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.model.Position;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PositionPlacementCategoryValueValidator
    implements ConstraintValidator<PositionPlacementCategoryValueConstraint, Position> {

  @Override
  public boolean isValid(Position position, ConstraintValidatorContext context) {
    return !position.getPlacementCategory().equals(PlacementCategory.NATIVE);
  }
}
