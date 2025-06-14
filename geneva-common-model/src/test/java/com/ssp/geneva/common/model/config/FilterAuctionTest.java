package com.ssp.geneva.common.model.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

class FilterAuctionTest {

  @Test
  void shouldReturnExpectedValue() {
    int valuelAll = FilterAuction.ALL.getValue();
    int valueFirstPrice = FilterAuction.FIRST_PRICE.getValue();
    int valueSecondPricePlus = FilterAuction.SECOND_PRICE_PLUS.getValue();
    assertEquals(0, valuelAll);
    assertEquals(1, valueFirstPrice);
    assertEquals(2, valueSecondPricePlus);
  }

  @Test
  void shouldReturnExpectedValues() {
    FilterAuction[] values = FilterAuction.values();
    assertEquals(3, values.length);
    assertEquals(FilterAuction.ALL, values[0]);
    assertEquals(FilterAuction.FIRST_PRICE, values[1]);
    assertEquals(FilterAuction.SECOND_PRICE_PLUS, values[2]);
  }

  @Test
  void shouldCheckValidValue() {
    assertTrue(FilterAuction.isValidValue(0));
    assertTrue(FilterAuction.isValidValue(1));
    assertTrue(FilterAuction.isValidValue(2));
  }

  @Test
  void shouldFailInvalidValue() {
    Random random = new Random();
    assertFalse(FilterAuction.isValidValue(-1));
    assertFalse(FilterAuction.isValidValue(3));
  }
}
