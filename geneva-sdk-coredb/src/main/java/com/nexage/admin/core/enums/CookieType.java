package com.nexage.admin.core.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum CookieType {
  APID(1),
  BCOOKIE(2),
  BOTH(3);

  private int value;

  CookieType(int value) {
    this.value = value;
  }

  public static CookieType valueOf(int value) {
    return cookieTypeMap.get(value);
  }

  private static final Map<Integer, CookieType> cookieTypeMap =
      Arrays.stream(CookieType.values())
          .collect(Collectors.toMap(CookieType::getValue, Function.identity()));
}
