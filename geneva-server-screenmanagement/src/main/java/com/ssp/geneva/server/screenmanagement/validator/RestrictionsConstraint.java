package com.ssp.geneva.server.screenmanagement.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = RestrictionsValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface RestrictionsConstraint {
  String message() default "Contains one or more unsupported restrictions: ${validatedValue}";

  String field() default "restrictions";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
