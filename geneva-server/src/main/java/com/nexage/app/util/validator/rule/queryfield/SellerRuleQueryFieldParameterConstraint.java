package com.nexage.app.util.validator.rule.queryfield;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = {SellerRuleQueryFieldParameterValidator.class})
@Target({TYPE})
@Retention(RUNTIME)
public @interface SellerRuleQueryFieldParameterConstraint {

  String message() default "Seller Rule Query Field parameter is invalid";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
