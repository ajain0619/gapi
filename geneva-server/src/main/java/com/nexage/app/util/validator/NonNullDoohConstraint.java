package com.nexage.app.util.validator;

import static com.nexage.app.util.validator.ValidationMessages.WRONG_IS_EMPTY;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.util.validator.placement.PlacementDTONonNullDoohValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Definition validation mechanism for an entity that can contain a {@link
 * com.nexage.app.dto.seller.PlacementDoohDTO}. Validator contains validation logic for correct Site
 * fields
 */
@Documented
@Constraint(
    validatedBy = {
      PublisherPositionDTONonNullDoohValidator.class,
      PlacementDTONonNullDoohValidator.class
    })
@Target(TYPE)
@Retention(RUNTIME)
public @interface NonNullDoohConstraint {
  String message() default WRONG_IS_EMPTY;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
