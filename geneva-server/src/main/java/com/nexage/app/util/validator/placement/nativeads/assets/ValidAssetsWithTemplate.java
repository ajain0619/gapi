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
@Constraint(validatedBy = {ValidAssetsWithTemplateValidator.class})
public @interface ValidAssetsWithTemplate {
  String message() default "There is a problem with Assets and the provided HTML Template: ";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
