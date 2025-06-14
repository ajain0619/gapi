package com.ssp.geneva.sdk.xandr.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.ssp.geneva.sdk.xandr.validator.CurrencyCodeValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = CurrencyCodeValidator.class)
public @interface CurrencyCode {
  String message() default "Invalid ISO 4217 code of currency.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
