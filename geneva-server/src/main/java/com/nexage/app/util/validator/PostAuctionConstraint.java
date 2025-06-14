package com.nexage.app.util.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = PostAuctionDealValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RUNTIME)
public @interface PostAuctionConstraint {

  String message() default "Error in PostAuctionDiscountDTO properties";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
