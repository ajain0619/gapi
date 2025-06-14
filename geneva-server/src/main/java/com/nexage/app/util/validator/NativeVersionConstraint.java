package com.nexage.app.util.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/** Constrains the string representation of a native version. */
@Documented
@Constraint(validatedBy = {NativeVersionValidator.class})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NativeVersionConstraint {
  String message() default "unknown native version";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
