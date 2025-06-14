package com.nexage.app.util.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.app.dto.publisher.PublisherSiteDTO;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Definition validation mechanism for a {@link PublisherSiteDTO} entity. Contains validation on
 * entity iabCategories field.
 */
@Documented
@Constraint(validatedBy = {SiteIABCategoriesValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RUNTIME)
public @interface SiteIabCategoriesConstraint {
  String message() default "SiteIabCategoriesConstraint";

  int min() default 0;

  int max() default Integer.MAX_VALUE;

  boolean nullable() default true;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
