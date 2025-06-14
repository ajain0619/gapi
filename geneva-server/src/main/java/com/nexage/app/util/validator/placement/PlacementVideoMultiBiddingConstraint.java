package com.nexage.app.util.validator.placement;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.util.validator.ValidationMessages;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = PlacementVideoMultiBiddingValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface PlacementVideoMultiBiddingConstraint {
  String message() default ValidationMessages.WRONG_VALUE;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
