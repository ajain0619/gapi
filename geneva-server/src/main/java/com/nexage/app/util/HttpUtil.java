package com.nexage.app.util;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpUtil {
  // HTTP header names should contain no control characters, space characters, or separators, as
  // defined in RFC 2616.
  // https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
  // https://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html#sec2.2
  private static final Pattern HTTP_HEADER_NAME_INVALID_CHAR_DETECTOR =
      Pattern.compile(".*[\\s\\p{Cntrl}()<>@,;:\\\\\"/\\[\\]?={}]+.*");

  public static boolean httpHeaderNameContainsInvalidCharacters(String name) {
    return name != null && HTTP_HEADER_NAME_INVALID_CHAR_DETECTOR.matcher(name).matches();
  }
}
