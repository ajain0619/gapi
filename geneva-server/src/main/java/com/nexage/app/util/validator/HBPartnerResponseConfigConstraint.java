package com.nexage.app.util.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.services.validation.HBPartnerResponseConfigValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = HBPartnerResponseConfigValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface HBPartnerResponseConfigConstraint {

  String field() default "responseConfig";

  String message() default "Invalid Response Config";

  Class<?>[] groups() default {};

  Class<?>[] payload() default {};
}
