package com.nexage.app.util.validator.smartexchangeattributes;

import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import com.nexage.app.util.validator.BaseValidator;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.ConstraintValidatorContext;

public class SmartExchangeAttributesValidator
    extends BaseValidator<SmartExchangeAttributesConstraint, SmartExchangeAttributesDTO> {

  private static final String FIELD_PRESENT = "Field '%s' must be present";
  private static final String FIELD_BETWEEN = "Field '%s' must have a value between %s and %s";
  private static final String MAX_REV_SHARE_GREATER =
      "Field 'smartMarginMaxRevShare' must be greater than 'smartMarginMinRevShare'";

  @Override
  public boolean isValid(
      SmartExchangeAttributesDTO smartExchangeAttributes,
      ConstraintValidatorContext constraintValidatorContext) {
    return true;
  }

  private ValidationPredicate<BigDecimal, ConstraintValidatorContext, String> notNull() {
    return (value, constraintValidatorContext, fieldName) -> {
      if (value == null) {
        addConstraintMessage(
            constraintValidatorContext, fieldName, String.format(FIELD_PRESENT, fieldName));
        return false;
      }
      return true;
    };
  }

  private ValidationPredicate<BigDecimal, ConstraintValidatorContext, String> between(
      BigDecimal minLimit, BigDecimal maxLimit) {
    return (value, constraintValidatorContext, fieldName) -> {
      if (value.compareTo(minLimit) >= 0 && value.compareTo(maxLimit) <= 0) {
        return true;
      }

      addConstraintMessage(
          constraintValidatorContext,
          fieldName,
          String.format(FIELD_BETWEEN, fieldName, minLimit, maxLimit));
      return false;
    };
  }

  @FunctionalInterface
  private interface ValidationPredicate<T, U, K> {

    boolean test(T t, U u, K k);

    default ValidationPredicate<T, U, K> and(
        ValidationPredicate<? super T, ? super U, ? super K> other) {
      Objects.requireNonNull(other);
      return (T t, U u, K k) -> test(t, u, k) && other.test(t, u, k);
    }
  }
}
