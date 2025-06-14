package com.nexage.app.util.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = {PublisherAndSiteAssociationTypeValidator.class})
@Target({FIELD})
@Retention(RUNTIME)
public @interface PublisherAndSiteAssociationTypeConstraint {
  String message() default "Invalid hb partner association type";

  String emptyMessage() default ValidationMessages.WRONG_IS_EMPTY;

  String field() default "type";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
