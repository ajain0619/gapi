package com.nexage.app.util.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = SearchRequestParameterValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SearchRequestParamConstraint {
  String message() default "Search request parameter has invalid type";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String[] allowedParams() default {};
}
