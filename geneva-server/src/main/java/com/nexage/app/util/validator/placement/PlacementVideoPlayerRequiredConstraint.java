package com.nexage.app.util.validator.placement;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.util.validator.ValidationMessages;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = PlacementVideoPlayerRequiredValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface PlacementVideoPlayerRequiredConstraint {
  String message() default ValidationMessages.PLACEMENT_VIDEO_PLAYER_HEIGHT_WIDTH_NOT_NULL;

  String emptyMessage() default ValidationMessages.WRONG_IS_EMPTY;

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
