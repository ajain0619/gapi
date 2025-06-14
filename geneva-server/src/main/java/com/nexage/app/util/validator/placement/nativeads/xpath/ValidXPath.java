package com.nexage.app.util.validator.placement.nativeads.xpath;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {XPathValidator.class})
public @interface ValidXPath {
  String message() default "Invalid XPath value";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
