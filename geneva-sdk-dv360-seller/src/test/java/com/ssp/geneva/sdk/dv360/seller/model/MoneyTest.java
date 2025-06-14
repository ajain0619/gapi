package com.ssp.geneva.sdk.dv360.seller.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MoneyTest {

  @Test
  void whenBuildMoney_thenReturnCorrectMoney() {
    Money money = new Money();
    money.setCurrencyCode("USD");
    money.setNanos(0);
    money.setUnits("100");

    assertEquals(money, Money.buildMoney("USD", new BigDecimal(100D), new BigDecimal(0.01D)));
  }

  @Test
  void whenBuildMoneyWithNanosValue_thenReturnCorrectMoney() {
    Money money = new Money();
    money.setCurrencyCode("USD");
    money.setNanos(1000000000);
    money.setUnits("99");

    assertEquals(money, Money.buildMoney("USD", new BigDecimal(99.999D), new BigDecimal(0.01D)));
  }

  @Test
  void whenBuildMoneyNullAmount_thenReturnCorrectMoney() {
    Money money = new Money();
    money.setCurrencyCode("USD");
    money.setNanos(10000000);
    money.setUnits("0");

    assertEquals(money, Money.buildMoney("USD", null, new BigDecimal(0.01D)));
  }

  @ParameterizedTest
  @CsvSource({"'USD', 0", "null , 1", "''   , 1", "'---', 1"})
  void whenNewMoney_thenCheckConstraintViolations(String code, int count) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    Money money = new Money();
    money.setCurrencyCode(code);
    money.setNanos(10000000);
    money.setUnits("0");

    Set<ConstraintViolation<Money>> violations = validator.validate(money);
    assertEquals(count, violations.size());
  }

  @ParameterizedTest
  @CsvSource({"'USD', 0", "null , 1", "''   , 1", "'---', 1"})
  void whenBuildMoney_thenCheckConstraintViolations(String code, int count) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    Money money = Money.buildMoney(code, BigDecimal.valueOf(0.01D), BigDecimal.valueOf(0.01D));

    Set<ConstraintViolation<Money>> violations = validator.validate(money);
    assertEquals(count, violations.size());
  }
}
