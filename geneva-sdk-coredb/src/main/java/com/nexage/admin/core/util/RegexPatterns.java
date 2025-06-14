package com.nexage.admin.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Eugeny Yurko
 * @since 31.10.2014
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexPatterns {

  public static final String ADOMAIN_PATTERN =
      "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,63}$";
}
