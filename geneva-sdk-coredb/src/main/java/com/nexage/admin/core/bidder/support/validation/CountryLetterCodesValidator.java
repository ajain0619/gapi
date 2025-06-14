package com.nexage.admin.core.bidder.support.validation;

import com.nexage.admin.core.bidder.support.validation.annotation.CountryLetterCodes;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Custom validator for a comma separated list of countries.
 *
 * @author Nikolay Ilkevich
 * @since 29.08.2014
 */
public class CountryLetterCodesValidator
    implements ConstraintValidator<CountryLetterCodes, String> {

  private static final String THREE_LETTERS_PATTERN = "[A-Z]{3}|(([A-Z]{3},)+[A-Z]{3})";

  private Pattern compiledPattern;

  @Override
  public void initialize(CountryLetterCodes constraintAnnotation) {
    compiledPattern = Pattern.compile(THREE_LETTERS_PATTERN);
  }

  @Override
  public boolean isValid(String list, ConstraintValidatorContext context) {
    if (list == null) {
      return true;
    } else {
      return StringUtils.isNotEmpty(list) && compiledPattern.matcher(list).matches();
    }
  }
}
