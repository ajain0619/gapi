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
@Constraint(validatedBy = PlacementVideoLongformValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface PlacementVideoLongformConstraint {

  String message() default ValidationMessages.WRONG_VALUE;

  String longformViolationMessage() default
      ValidationMessages.PLACEMENT_VIDEO_LONGFORM_CONSTRAINT_VIOLATION;

  String streamTypeViolationMessage() default
      ValidationMessages.PLACEMENT_VIDEO_STREAM_TYPE_CONSTRAINT_VIOLATION;

  String playerBrandViolationMessage() default
      ValidationMessages.PLACEMENT_VIDEO_PLAYER_BRAND_CONSTRAINT_VIOLATION;

  String ssaiViolationMessage() default
      ValidationMessages.PLACEMENT_VIDEO_SSAI_CONSTRAINT_VIOLATION;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
