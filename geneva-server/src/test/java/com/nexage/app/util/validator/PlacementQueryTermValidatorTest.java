package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

class PlacementQueryTermValidatorTest {

  @Test
  void isValidIfSearchCorrect() {
    boolean result =
        PlacementQueryTermValidator.isValid(
            ImmutableMap.of(
                "action", "search",
                "name", "test"));
    assertTrue(result);
  }

  @Test
  void isValidIfDuplicateCorrect() {
    boolean result =
        PlacementQueryTermValidator.isValid(
            ImmutableMap.of(
                "action", "duplicate",
                "name", "test"));
    assertTrue(result);
  }

  @Test
  void isInvalidIfQtDoNotExist() {
    boolean result = PlacementQueryTermValidator.isValid(null);
    assertFalse(result);
  }

  @Test
  void isInvalidIfSearchTermEmpty() {
    boolean result = PlacementQueryTermValidator.isValid(ImmutableMap.of());
    assertFalse(result);
  }

  @Test
  void isInvalidIfSearchLengthIncorrect() {
    boolean result = PlacementQueryTermValidator.isValid(ImmutableMap.of("action", "search"));
    assertFalse(result);
  }

  @Test
  void isInvalidIfDuplicateLengthIncorrect() {
    boolean result = PlacementQueryTermValidator.isValid(ImmutableMap.of("action", "duplicate"));
    assertFalse(result);
  }

  @Test
  void isInvalidIfDuplicateTypeIncorrect() {
    boolean result =
        PlacementQueryTermValidator.isValid(
            ImmutableMap.of(
                "action", "duplicate",
                "id", "test"));
    assertFalse(result);
  }
}
