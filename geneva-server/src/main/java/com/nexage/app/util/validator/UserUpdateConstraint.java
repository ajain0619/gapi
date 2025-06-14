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
@Constraint(validatedBy = {UserUpdateValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserUpdateConstraint {

  String message() default "Invalid User Details";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
