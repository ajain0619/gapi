package com.nexage.app.util.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = FormulaRuleValidator.class)
@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface FormulaRuleConstraint {

  String message() default "Error in Placement Formula Rule";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
