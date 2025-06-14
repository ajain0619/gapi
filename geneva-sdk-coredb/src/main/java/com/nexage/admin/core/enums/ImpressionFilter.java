package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum ImpressionFilter {
  BANNER("1"),
  VIDEO("2"),
  NATIVE("3");

  private String value;

  ImpressionFilter(String value) {
    this.value = value;
  }

  public String asActual() {
    return value;
  }

  public static ImpressionFilter fromActual(String value) {
    return fromImpressionMap.get(value);
  }

  private static final HashMap<String, ImpressionFilter> fromImpressionMap = new HashMap<>();

  static {
    for (ImpressionFilter impr : ImpressionFilter.values()) {
      fromImpressionMap.put(impr.value, impr);
    }
  }
}
