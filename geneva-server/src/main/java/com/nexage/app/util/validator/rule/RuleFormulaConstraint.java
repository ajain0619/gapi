package com.nexage.app.util.validator.rule;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = RuleFormulaValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface RuleFormulaConstraint {

  String message() default "Bad rule data";

  String messageNullAttributePid() default
      "Placement formula rule is Inventory Attribute type but null or bad attribute pid";

  String messageMoreThanOneArgumentOccurrence() default
      "More then one attribute occurrence in a group";

  Class<?>[] groups() default {};

  Class<?>[] payload() default {};
}
