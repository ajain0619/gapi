package com.nexage.app.util.validator.placement.nativeads.assets;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {RulesOnAssetSetValidator.class})
public @interface RulesOnAssetSet {
  String message() default
      "Only one Require-All Asset Set and (optional) one Optional Asset Set are allowed for Web Native Placement";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
