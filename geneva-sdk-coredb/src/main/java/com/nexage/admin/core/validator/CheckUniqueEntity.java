package com.nexage.admin.core.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.validator.impl.CheckUniqueEntityValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({METHOD, FIELD, TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = CheckUniqueEntityValidator.class)
@Documented
public @interface CheckUniqueEntity {

  String message() default "{com.nexage.admin.validator.Checkunique.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String[] properties();

  CoreDBErrorCodes errorCode();
}
