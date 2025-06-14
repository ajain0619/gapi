package com.ssp.geneva.sdk.dv360.seller.validator;

import com.ssp.geneva.sdk.dv360.seller.annotation.CurrencyCode;
import java.util.Currency;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CurrencyCodeValidator implements ConstraintValidator<CurrencyCode, String> {

  @Override
  public void initialize(CurrencyCode constraintAnnotation) {
    log.debug("CurrencyCodeValidator.initialize()");
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(
      String currencyCode, ConstraintValidatorContext constraintValidatorContext) {
    log.debug("CurrencyCodeValidator.isValid('{}')", currencyCode);
    return Currency.getAvailableCurrencies().stream()
        .anyMatch(c -> c.getCurrencyCode().equals(currencyCode));
  }
}
