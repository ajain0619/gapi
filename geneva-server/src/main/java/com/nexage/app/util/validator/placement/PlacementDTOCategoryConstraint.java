package com.nexage.app.util.validator.placement;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.util.validator.ValidationMessages;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(
    validatedBy = {
      PlacementDTOCategoryValidator.class,
      PublisherPositionDTOCategoryValidator.class
    })
@Target({TYPE})
@Retention(RUNTIME)
public @interface PlacementDTOCategoryConstraint {

  String message() default "Invalid placementCategory for site type and/or platform type";

  String emptyMessage() default ValidationMessages.WRONG_IS_EMPTY;

  String field() default "placementCategory";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
