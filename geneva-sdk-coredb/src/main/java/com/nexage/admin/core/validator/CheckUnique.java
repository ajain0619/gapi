package com.nexage.admin.core.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.validator.impl.CheckUniqueValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/** Define an annotation to validate uniqueness of the data */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(
    validatedBy =
        CheckUniqueValidator
            .class) // Link between a constraint annotation and its constraint validation
// implementations
@Documented
public @interface CheckUnique {

  // Defines the error message that should be used in case the constraint is violated.
  String message() default "{com.nexage.admin.validator.Checkunique.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String fieldName();

  Class<?> entity();

  CoreDBErrorCodes errorCode();
}
