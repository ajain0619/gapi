package com.nexage.app.util.validator;

import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;

/** Contains functionality for testing bean validation. */
public class ValidationTestUtil {

  /** Indicates an expected violation was not present. */
  public static class MissingViolationException extends AssertionError {
    private MissingViolationException(String propertyPath, String message) {
      super(
          String.format(
              "expected but not present: violation with propertyPath = \"%s\" and message = \"%s\"",
              propertyPath, message));
    }
  }

  /** Indicates unexpected violation message */
  public static class UnexpectedViolationException extends AssertionError {
    private UnexpectedViolationException(String propertyPath, String message) {
      super(
          String.format(
              "not expected: violation with propertyPath = \"%s\" and message = \"%s\"",
              propertyPath, message));
    }
  }

  /**
   * Asserts that one of the violations has the given property path and message.
   *
   * @param violations violations to check
   * @param propertyPath expected property path
   * @param message expected message
   * @param <T> type being validated
   * @throws MissingViolationException if no violation with {@code propertyPath} and {@code message}
   */
  public static <T> void assertViolationsContains(
      Set<ConstraintViolation<T>> violations, String propertyPath, String message) {
    violations.stream()
        .filter(violation -> violation.getPropertyPath().toString().equals(propertyPath))
        .filter(violation -> violation.getMessage().equals(message))
        .findAny()
        .orElseThrow(() -> new MissingViolationException(propertyPath, message));
  }

  /**
   * Asserts the violations should not contain given property path and message.
   *
   * @param violations violations to check
   * @param propertyPath property path
   * @param message unexpected message
   * @param <T> type being validated
   * @throws UnexpectedViolationException if violation present with {@code propertyPath} and {@code
   *     message}
   */
  public static <T> void assertViolationsNotContain(
      Set<ConstraintViolation<T>> violations, String propertyPath, String message) {
    if (violations.stream()
        .filter(violation -> violation.getPropertyPath().toString().equals(propertyPath))
        .anyMatch(violation -> violation.getMessage().equals(message))) {
      throw new UnexpectedViolationException(propertyPath, message);
    }
  }

  /**
   * Returns a string of the given length.
   *
   * @param length the length of the returned string
   * @return the string of the given length
   */
  public static String stringOfLength(int length) {
    char[] chars = new char[length];
    Arrays.fill(chars, 'a');
    return String.valueOf(chars);
  }
}
