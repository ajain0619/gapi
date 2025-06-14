package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;

class ExchangeRateTest {

  private static final String EUR = "EUR";
  private static final BigDecimal RATE = new BigDecimal("0.98");
  private static final BigDecimal RATE_2 = new BigDecimal("0.97");
  private static final Date CHECK_DATE = new Date();

  ExchangeRate createExchangeRate() {
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);
    ExchangeRate exchangeRate = new ExchangeRate(exchangeRatePrimaryKey, RATE, 1L);
    return exchangeRate;
  }

  @Test
  void shouldCreateExchangeRateWithAllArgsConstructor() {
    // given
    ExchangeRate exchangeRate = createExchangeRate();

    // then
    assertEquals(EUR, exchangeRate.getId().getCurrency());
    assertEquals(CHECK_DATE, exchangeRate.getId().getCheckDate());
    assertEquals(1L, exchangeRate.getForexId());
    assertEquals(RATE, exchangeRate.getRate());
  }

  @Test
  void shouldCreateExchangeRateWithNoArgsConstructor() {
    // given
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);
    ExchangeRate exchangeRate = new ExchangeRate();
    exchangeRate.setId(exchangeRatePrimaryKey);
    exchangeRate.setRate(RATE);
    exchangeRate.setForexId(1L);

    // then
    assertEquals(EUR, exchangeRate.getId().getCurrency());
    assertEquals(CHECK_DATE, exchangeRate.getId().getCheckDate());
    assertEquals(1L, exchangeRate.getForexId());
    assertEquals(RATE, exchangeRate.getRate());
  }

  @Test
  void shouldEqualReturnTrueWhenObjectIsComparedToItself() {
    // given
    ExchangeRate exchangeRate = createExchangeRate();

    // when
    var objectsAreEqual = exchangeRate.equals(exchangeRate);

    // then
    assertTrue(objectsAreEqual);
  }

  @Test
  void shouldEqualReturnFalseWhenObjectIsNotOfExchangeRateType() {
    // given
    ExchangeRate exchangeRate = createExchangeRate();

    // when
    var objectsAreEqual = exchangeRate.equals(new Object());

    // then
    assertFalse(objectsAreEqual);
  }

  @Test
  void shouldEqualReturnTrueWhenObjectsAreEqual() {
    // given
    ExchangeRate exchangeRate = createExchangeRate();
    ExchangeRate exchangeRate1 = createExchangeRate();

    // when
    var objectsAreEqual = exchangeRate.equals(exchangeRate1);

    // then
    // equals() and hashCode() behavior must be consistent
    assertTrue(objectsAreEqual);
    assertEquals(exchangeRate.hashCode(), exchangeRate1.hashCode());
  }

  @Test
  void shouldEqualReturnFalseWhenExchangeRatesHaveDifferentId() {
    // given
    ExchangeRate exchangeRate = createExchangeRate();
    ExchangeRatePrimaryKey exchangeRatePrimaryKey =
        new ExchangeRatePrimaryKey(EUR, Date.from(Instant.parse("2022-11-17T10:15:30.00Z")));
    ExchangeRate exchangeRate1 = new ExchangeRate(exchangeRatePrimaryKey, RATE_2, 2L);

    // when
    var objectsAreEqual = exchangeRate.equals(exchangeRate1);

    // then
    // equals() and hashCode() behavior must be consistent
    assertFalse(objectsAreEqual);
    assertNotEquals(exchangeRate.hashCode(), exchangeRate1.hashCode());
  }

  @Test
  void shouldEqualReturnFalseWhenExchangeRatesHaveSameIdButDifferentRate() {
    // given
    ExchangeRate exchangeRate = createExchangeRate();
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);
    ExchangeRate exchangeRate1 = new ExchangeRate(exchangeRatePrimaryKey, RATE_2, 2L);

    // when
    var objectsAreEqual = exchangeRate.equals(exchangeRate1);

    // then
    // equals() and hashCode() behavior must be consistent
    assertFalse(objectsAreEqual);
    assertNotEquals(exchangeRate.hashCode(), exchangeRate1.hashCode());
  }

  @Test
  void shouldEqualReturnFalseWhenExchangeRatesHaveSameIdAndSameRateButDifferentForexId() {
    // given
    ExchangeRate exchangeRate = createExchangeRate();
    ExchangeRatePrimaryKey exchangeRatePrimaryKey = new ExchangeRatePrimaryKey(EUR, CHECK_DATE);
    ExchangeRate exchangeRate1 = new ExchangeRate(exchangeRatePrimaryKey, RATE, 2L);

    // when
    var objectsAreEqual = exchangeRate.equals(exchangeRate1);

    // then
    // equals() and hashCode() behavior must be consistent
    assertFalse(objectsAreEqual);
    assertNotEquals(exchangeRate.hashCode(), exchangeRate1.hashCode());
  }
}
