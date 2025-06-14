package com.nexage.app.util.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/** Custom constraints applicable to UserDTO objects */
@Documented
@Constraint(validatedBy = {IdentityIqUserValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdentityIqUserConstraint {

  String message() default ValidationMessages.IDENTITYIQ_USER_VALIDATION_INVALID_USER_DETAILS;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
