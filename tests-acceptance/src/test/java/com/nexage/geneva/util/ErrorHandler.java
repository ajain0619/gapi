package com.nexage.geneva.util;

import com.nexage.geneva.request.Request;
import org.junit.jupiter.api.Assertions;
import org.springframework.util.StringUtils;

public class ErrorHandler {

  public static void assertNotEmpty(Request request, String exceptionMessage) {
    if (!StringUtils.isEmpty(exceptionMessage)) {
      String errorMessage = buildErrorMessage(request, exceptionMessage);
      Assertions.assertTrue(false, errorMessage);
    }
  }

  public static void assertTrue(Request request, boolean condition, String exceptionMessage) {
    String errorMessage = buildErrorMessage(request, exceptionMessage);
    Assertions.assertTrue(condition, errorMessage);
  }

  public static void assertFalse(Request request, boolean condition, String exceptionMessage) {
    String errorMessage = buildErrorMessage(request, exceptionMessage);
    Assertions.assertFalse(condition, errorMessage);
  }

  public static void assertNull(Request request, Object value, String exceptionMessage) {
    String errorMessage = buildErrorMessage(request, exceptionMessage);
    Assertions.assertNull(value, errorMessage);
  }

  public static void assertNotNull(Request request, Object value, String exceptionMessage) {
    String errorMessage = buildErrorMessage(request, exceptionMessage);
    Assertions.assertNotNull(value, errorMessage);
  }

  private static String buildErrorMessage(Request request, String exceptionMessage) {
    return String.format("\nURL: %s\nException message:\n%s\n", request.getUrl(), exceptionMessage);
  }
}
