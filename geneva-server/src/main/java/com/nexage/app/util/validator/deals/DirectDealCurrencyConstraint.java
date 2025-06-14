package com.nexage.app.util.validator.deals;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Definition validation mechanism for a {@link com.nexage.app.dto.DirectDealDTO} entity. Contains
 * validation for deal currency
 */
@Documented
@Constraint(validatedBy = {DirectDealCurrencyValidator.class})
@Target({TYPE})
@Retention(RUNTIME)
public @interface DirectDealCurrencyConstraint {
  String message() default "{com.nexage.app.util.validator.deals.DirectDealCurrencyConstraint}";

  String field() default "currency";

  String currency() default "USD";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
