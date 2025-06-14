package com.nexage.app.util.validator.placement;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.util.validator.ValidationMessages;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = {PlacementDTODapValidator.class, PublisherPositionDTODapValidator.class})
@Target(TYPE)
@Retention(RUNTIME)
public @interface PlacementDTODapConstraint {

  String message() default ValidationMessages.WRONG_VALUE;

  String emptyMessage() default ValidationMessages.WRONG_IS_EMPTY;

  String field() default "placementCategory";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
