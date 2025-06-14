package com.nexage.app.util.validator.placement;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.util.validator.ValidationMessages;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = PlacementDTOVideoSettingsValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface PlacementDTOVideoSettingsConstraint {
  String message() default "Missing or invalid video fields";

  String emptyMessage() default ValidationMessages.WRONG_IS_EMPTY;

  String invalidPlacementCategoryForLongform() default
      "Placement category must be INSTREAM_VIDEO for enabling longform";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
