package com.nexage.app.util.validator.site;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = {SiteQueryFieldParameterValidator.class})
@Target({TYPE})
@Retention(RUNTIME)
public @interface SiteQueryFieldParameterConstraint {

  String message() default "Site Query Field parameter is invalid";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
