package com.ssp.geneva.common.model.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

class BidUnitTest {

  @Test
  void shouldReturnExpectedValue() {
    int valuelUsdCpm = BidUnit.USD_CPM.getValue();
    int valueUsdPerUnit = BidUnit.USD_PER_UNIT.getValue();
    int valueUsdMicrosPerUnit = BidUnit.USD_MICROS_PER_UNIT.getValue();
    assertEquals(0, valuelUsdCpm);
    assertEquals(1, valueUsdPerUnit);
    assertEquals(2, valueUsdMicrosPerUnit);
  }

  @Test
  void shouldReturnExpectedValues() {
    BidUnit[] values = BidUnit.values();
    assertEquals(3, values.length);
    assertEquals(BidUnit.USD_CPM, values[0]);
    assertEquals(BidUnit.USD_PER_UNIT, values[1]);
    assertEquals(BidUnit.USD_MICROS_PER_UNIT, values[2]);
  }

  @Test
  void shouldCheckValidValue() {
    assertTrue(BidUnit.isValidValue(0));
    assertTrue(BidUnit.isValidValue(1));
    assertTrue(BidUnit.isValidValue(2));
  }

  @Test
  void shouldFailInvalidValue() {
    Random random = new Random();
    assertFalse(BidUnit.isValidValue(-1));
    assertFalse(BidUnit.isValidValue(3));
  }
}
