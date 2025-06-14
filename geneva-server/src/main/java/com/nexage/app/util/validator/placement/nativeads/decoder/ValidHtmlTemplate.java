package com.nexage.app.util.validator.placement.nativeads.decoder;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {HtmlTemplateValidator.class})
public @interface ValidHtmlTemplate {
  String message() default "HTML Template value is not a valid HTML";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
