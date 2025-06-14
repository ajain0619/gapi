package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;

class ExchangeRatePrimaryKeyTest {

  private static final String EUR = "EUR";
  private static final Date CHECK_DATE = new Date();

  @Test
  void shouldCreateExchangeRatePrimaryKeyWithAllArgsConstructor() {
    // given
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);

    // then
    assertEquals(EUR, exchangeRatePrimaryKey.getCurrency());
    assertEquals(CHECK_DATE, exchangeRatePrimaryKey.getCheckDate());
  }

  @Test
  void shouldCreateExchangeRatePrimaryKeyWithNoArgsConstructor() {
    // given
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey();
    exchangeRatePrimaryKey.setCurrency(EUR);
    exchangeRatePrimaryKey.setCheckDate(CHECK_DATE);

    // then
    assertEquals(EUR, exchangeRatePrimaryKey.getCurrency());
    assertEquals(CHECK_DATE, exchangeRatePrimaryKey.getCheckDate());
  }

  @Test
  void shouldEqualReturnTrueWhenObjectIsComparedToItself() {
    // given
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);

    // when
    var objectsAreEqual = exchangeRatePrimaryKey.equals(exchangeRatePrimaryKey);

    // then
    assertTrue(objectsAreEqual);
  }

  @Test
  void shouldEqualReturnFalseWhenObjectIsNotOfExchangeRateType() {
    // given
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);

    // when
    var objectsAreEqual = exchangeRatePrimaryKey.equals(new Object());

    // then
    assertFalse(objectsAreEqual);
  }

  @Test
  void shouldEqualReturnTrueWhenObjectsAreEqual() {
    // given
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);
    ExchangeRatePrimaryKey exchangeRatePrimaryKey1 = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);

    // when
    var objectsAreEqual = exchangeRatePrimaryKey.equals(exchangeRatePrimaryKey1);

    // then
    // equals() and hashCode() behavior must be consistent
    assertTrue(objectsAreEqual);
    assertEquals(exchangeRatePrimaryKey.hashCode(), exchangeRatePrimaryKey1.hashCode());
  }

  @Test
  void shouldEqualReturnFalseWhenExchangeRatesHaveSameCurrencyButDifferentCheckDate() {
    // given
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);
    ExchangeRatePrimaryKey exchangeRatePrimaryKey1 =
        new ExchangeRatePrimaryKey(EUR, Date.from(Instant.parse("2022-11-17T10:15:30.00Z")));

    // when
    var objectsAreEqual = exchangeRatePrimaryKey.equals(exchangeRatePrimaryKey1);

    // then
    // equals() and hashCode() behavior must be consistent
    assertFalse(objectsAreEqual);
    assertNotEquals(exchangeRatePrimaryKey.hashCode(), exchangeRatePrimaryKey1.hashCode());
  }

  @Test
  void shouldEqualReturnFalseWhenExchangeRatesHaveSameCheckDateButDifferentCurrency() {
    // given
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);
    ExchangeRatePrimaryKey exchangeRatePrimaryKey1 = new ExchangeRatePrimaryKey("GBP", CHECK_DATE);

    // when
    var objectsAreEqual = exchangeRatePrimaryKey.equals(exchangeRatePrimaryKey1);

    // then
    // equals() and hashCode() behavior must be consistent
    assertFalse(objectsAreEqual);
    assertNotEquals(exchangeRatePrimaryKey.hashCode(), exchangeRatePrimaryKey1.hashCode());
  }

  @Test
  void shouldGenerateCorrectStringRepresentation() {
    // given
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);

    // then
    assertEquals(
        "ExchangeRatePrimaryKey(currency=EUR, checkDate=" + CHECK_DATE + ")",
        exchangeRatePrimaryKey.toString());
  }
}
