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
@Constraint(
    validatedBy = {
      PlacementDTOVideoSupportValidator.class,
      PublisherPositionDTOVideoSupportValidator.class
    })
@Target(TYPE)
@Retention(RUNTIME)
/** Validate videoSupport for {@link PlacementDTO} object */
public @interface PlacementDTOVideoSupportConstraint {

  String message() default
      "Invalid for specified site type, platform type and/or placementCategory";

  String emptyMessage() default ValidationMessages.WRONG_IS_EMPTY;

  String field() default "videoSupport";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
