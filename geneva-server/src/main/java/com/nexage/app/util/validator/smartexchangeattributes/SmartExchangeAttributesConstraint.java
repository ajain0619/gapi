package com.nexage.app.util.validator.smartexchangeattributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Constraint(validatedBy = SmartExchangeAttributesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartExchangeAttributesConstraint {

  String message() default "";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
