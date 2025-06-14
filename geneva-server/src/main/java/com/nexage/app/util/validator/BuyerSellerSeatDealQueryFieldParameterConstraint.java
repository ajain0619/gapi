package com.nexage.app.util.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = {BuyerSellerSeatDealQueryParameterValidator.class})
@Target({TYPE})
@Retention(RUNTIME)
public @interface BuyerSellerSeatDealQueryFieldParameterConstraint {

  String message() default ValidationMessages.INVALID_SELLER_BUYER_SEAT_VALUE;

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
