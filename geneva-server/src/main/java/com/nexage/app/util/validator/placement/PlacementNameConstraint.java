package com.nexage.app.util.validator.placement;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.util.validator.ValidationMessages;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = {PlacementNameValidator.class})
@Target({FIELD})
@Retention(RUNTIME)
public @interface PlacementNameConstraint {

  String message() default ValidationMessages.WRONG_STRING_PATTERN;

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
