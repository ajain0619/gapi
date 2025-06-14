package com.ssp.geneva.server.screenmanagement.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = Iso2StateCodeValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface Iso2StateCodeConstraint {
  String message() default "Unsupported ISO 2 state code: ${validatedValue}";

  String field() default "state";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
