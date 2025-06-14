package com.nexage.app.util.validator.publisherattributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Constraint(validatedBy = PublisherAttributesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PublisherAttributesConstraint {

  String message() default "";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
