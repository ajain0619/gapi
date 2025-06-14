package com.ssp.geneva.server.screenmanagement.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = ZipCodeValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface ZipCodeConstraint {

  String message() default "Unsupported zip code: ${validatedValue}";

  String field() default "zip";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
