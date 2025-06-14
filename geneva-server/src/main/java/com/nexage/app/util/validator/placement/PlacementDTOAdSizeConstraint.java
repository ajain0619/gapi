package com.nexage.app.util.validator.placement;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.util.validator.ValidationMessages;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Definition validation mechanism for a {@link com.nexage.app.dto.seller.PlacementDTO} entity.
 * Validator contains validation logic for width, height fields
 */
@Documented
@Constraint(validatedBy = {PlacementDTOAdSizeValidator.class})
@Target({TYPE})
@Retention(RUNTIME)
public @interface PlacementDTOAdSizeConstraint {
  String message() default
      "Placement AdSize (width and/or height) is invalid for placementCategory";

  String emptyMessage() default ValidationMessages.WRONG_IS_EMPTY;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
