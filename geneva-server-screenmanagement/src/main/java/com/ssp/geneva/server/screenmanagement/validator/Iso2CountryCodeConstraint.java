package com.ssp.geneva.server.screenmanagement.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = Iso2CountryCodeValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface Iso2CountryCodeConstraint {
  String message() default "Unsupported ISO 2 country code: ${validatedValue}";

  String field() default "country";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
