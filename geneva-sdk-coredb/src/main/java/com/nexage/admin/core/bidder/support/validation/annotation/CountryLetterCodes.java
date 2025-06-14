package com.nexage.admin.core.bidder.support.validation.annotation;

import com.nexage.admin.core.bidder.support.validation.CountryLetterCodesValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;

/**
 * See {@link com.nexage.admin.core.bidder.support.validation.CountryLetterCodesValidator} for
 * details.
 *
 * @author Nikolay Ilkevich
 * @since 29.08.2014
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
  ElementType.METHOD,
  ElementType.FIELD,
  ElementType.CONSTRUCTOR,
  ElementType.PARAMETER,
  ElementType.ANNOTATION_TYPE
})
@Constraint(validatedBy = CountryLetterCodesValidator.class)
public @interface CountryLetterCodes {

  String message() default "You must use a comma separated list of three letter country codes";

  Class[] groups() default {};

  Class[] payload() default {};
}
