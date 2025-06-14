package com.nexage.admin.core.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.admin.core.enums.PlacementCategory;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = {PositionPlacementCategoryValueValidator.class})
@Target({TYPE})
@Retention(RUNTIME)
public @interface PositionPlacementCategoryValueConstraint {
  PlacementCategory[] anyOf();

  String message() default "Must be any of {anyOf}";

  String field() default "placementCategory";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
