package com.nexage.admin.core.util;

import org.apache.commons.lang3.StringEscapeUtils;

/** Utility class for handling XSS cleanup operations. */
public final class XssSanitizerUtil {

  private XssSanitizerUtil() {
    // private constructor to prevent instantiation
  }

  /**
   * Sanitizes input string by escaping HTML characters.
   *
   * @param input input string
   * @return string with all HTML characters escaped
   */
  public static String sanitize(String input) {
    return StringEscapeUtils.escapeHtml4(input);
  }
}
