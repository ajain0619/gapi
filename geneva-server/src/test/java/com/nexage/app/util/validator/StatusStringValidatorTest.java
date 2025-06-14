package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class StatusStringValidatorTest {

  @ParameterizedTest
  @MethodSource("getInvalidValues")
  void shouldReturnFalseForInvalidValues(String params) {
    boolean result = StatusStringValidator.isValid(params);
    assertFalse(result);
  }

  @Test
  void isValidIfParamCorrect() {
    boolean result = StatusStringValidator.isValid("ACTIVE,INACTIVE");
    assertTrue(result);
  }

  private static Stream<String> getInvalidValues() {
    return Stream.of(null, "", "DELETED", "INVALID_STATUS");
  }
}
