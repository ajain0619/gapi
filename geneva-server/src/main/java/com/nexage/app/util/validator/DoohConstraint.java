package com.nexage.app.util.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.placement.PlacementDTODoohValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Definition validation mechanism for a {@link PlacementDTO} entity. Validator contains validation
 * logic for correct Site fields
 */
@Documented
@Constraint(
    validatedBy = {PlacementDTODoohValidator.class, PublisherPositionDTODoohValidator.class})
@Target(TYPE)
@Retention(RUNTIME)
public @interface DoohConstraint {
  String message() default "DOOH available for DOOH Sites only";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
