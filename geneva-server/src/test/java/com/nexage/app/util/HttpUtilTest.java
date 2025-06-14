package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class HttpUtilTest {

  // 1: HTTP header name to test, 2: expecting invalid character(s) in the name
  public static Collection<Object[]> data() {
    return Arrays.asList(
        new Object[][] {
          {"X-CRID-HEADER-NAME", false},
          {"x-content-type-options", false},
          {"alt-svc", false},
          {"alt-svc", false},
          {"-", false},
          {"", false},
          {null, false},
          {"x-invalid;", true},
          {"x-invalid@separator", true},
          {"x-invalid space", true},
          {"x-invalid\ttab", true},
          {"x-invalid\010control-char", true},
          {" ", true},
        });
  }

  @ParameterizedTest
  @MethodSource("data")
  void test_httpHeaderNameContainsInvalidCharacters(
      String headerName, boolean containsInvalidCharacters) {
    assertEquals(
        containsInvalidCharacters,
        HttpUtil.httpHeaderNameContainsInvalidCharacters(headerName),
        "Invalid characters in HTTP header name: " + headerName);
  }
}
