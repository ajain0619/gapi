package com.nexage.app.util.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = DomainValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface DomainConstraint {
  String message() default "Invalid Domain Format";

  String field() default "domain";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
