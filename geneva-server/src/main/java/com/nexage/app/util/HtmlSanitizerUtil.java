package com.nexage.app.util;

import static com.nexage.admin.core.util.XssSanitizerUtil.sanitize;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HtmlSanitizerUtil {

  private static final Pattern HTML_PATTERN = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>");

  /**
   * Validates if input string has HTML elements and sanitizes it.
   *
   * @param input input string
   * @return string with HTML element escaped if it matches the regex otherwise return the input
   */
  public static String sanitizeHtmlElement(String input) {
    if (StringUtils.isNotBlank(input) && HTML_PATTERN.matcher(input).find()) {
      return (sanitize(input));
    }
    return input;
  }
}
