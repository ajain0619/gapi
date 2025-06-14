package com.nexage.app.dto.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FilterTypeTest {
  @Test
  void shouldReturnTrue_whenEnumWithNameExists() {
    assertTrue(FilterType.enumValueWithNameExists("BLOCKLIST"));
  }

  @Test
  void shouldReturnFalse_whenEnumWithNameDoesNotExist() {
    assertFalse(FilterType.enumValueWithNameExists("NOT_A_FILTER_TYPE"));
  }

  @Test
  void shouldReturnValue_whenEnumExistsForName() {
    assertEquals("1", FilterType.convertEnumNameToValue("BLOCKLIST"));
  }

  @Test
  void shouldReturnNull_whenEnumDoesNotExistForName() {
    assertNull(FilterType.convertEnumNameToValue("NOT_A_FILTER_TYPE"));
  }

  @Test
  void shouldReturnEnumName_whenEnumExistsForValue() {
    assertEquals("BLOCKLIST", FilterType.convertValueToEnumName("1"));
  }

  @Test
  void shouldReturnNull_whenEnumDoesNotExistForValue() {
    assertNull(FilterType.convertValueToEnumName("3"));
  }
}
