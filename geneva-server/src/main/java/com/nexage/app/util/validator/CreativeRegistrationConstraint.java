package com.nexage.app.util.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Definition validation mechanism for a {@link com.nexage.app.dto.CreativeRegistrationDTO} entity.
 * Validator contains validation logic for correct sellerIds and dealIds fields
 */
@Documented
@Constraint(validatedBy = {CreativeRegistrationDTOValidator.class})
@Target(TYPE)
@Retention(RUNTIME)
public @interface CreativeRegistrationConstraint {
  String message() default "seller and deal ids cannot be empty";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
