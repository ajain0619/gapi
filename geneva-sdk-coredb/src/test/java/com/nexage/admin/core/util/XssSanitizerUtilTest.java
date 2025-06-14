package com.nexage.admin.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class XssSanitizerUtilTest {

  private static final String PLAIN_TEXT = "non-XSS input";

  private static final String SIMPLE_XSS_TEXT = "<script src='http://evli.com/bad.js'></script>";
  private static final String SIMPLE_XSS_TEXT_CLEAN =
      "&lt;script src='http://evli.com/bad.js'&gt;&lt;/script&gt;";

  @Test
  void verifyPlainText() {
    String out = XssSanitizerUtil.sanitize(PLAIN_TEXT);
    assertEquals(PLAIN_TEXT, out);
  }

  @Test
  void verifyTextSanitized() {
    String out = XssSanitizerUtil.sanitize(SIMPLE_XSS_TEXT);
    assertEquals(SIMPLE_XSS_TEXT_CLEAN, out);
  }
}
