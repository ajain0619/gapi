package com.nexage.app.util.validator.placement;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.util.validator.ValidationMessages;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Definition validation mechanism for a {@link com.nexage.app.dto.seller.PlacementDTO} entity.
 * Contains validation on entity interstitial.
 */
@Documented
@Constraint(validatedBy = PlacementDTOInterstitialBooleanValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface PlacementDTOInterstitialBooleanConstraint {

  String message() default ValidationMessages.WRONG_VALUE;

  String emptyMessage() default ValidationMessages.WRONG_IS_EMPTY;

  String field() default "interstitial";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
