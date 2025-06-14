package com.ssp.geneva.server.screenmanagement.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = DoohScreenCountryStateValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface DoohScreenCountryStateConstraint {
  String message() default "${validatedValue.country} country requires a state";

  String field() default "state";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
