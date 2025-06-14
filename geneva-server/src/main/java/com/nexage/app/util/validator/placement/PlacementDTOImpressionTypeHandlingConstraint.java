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
@Constraint(
    validatedBy = {
      PlacementDTOImpressionTypeHandlingValidator.class,
      PublisherPositionDTOImpressionTypeHandlingValidator.class
    })
@Target(TYPE)
@Retention(RUNTIME)
/** Validate impressionTypeHandling for {@link PlacementDTO} object */
public @interface PlacementDTOImpressionTypeHandlingConstraint {

  String message() default "Placement Impression Type Handling is invalid";

  String emptyMessage() default ValidationMessages.WRONG_IS_EMPTY;

  String field() default "impressionTypeHandling";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
