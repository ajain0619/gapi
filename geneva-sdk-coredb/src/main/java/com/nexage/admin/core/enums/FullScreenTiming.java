package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum FullScreenTiming {
  PRESTITIAL("Prestitial"),
  MIDSTITIAL("Midstitial"),
  POSTSTITIAL("Poststitial");

  private String value;

  FullScreenTiming(String timing) {
    this.value = timing;
  }

  public String asString() {
    return this.value;
  }

  public static FullScreenTiming fromString(String value) {
    return fromStringMap.get(value);
  }

  private static final HashMap<String, FullScreenTiming> fromStringMap = new HashMap<>();

  static {
    for (FullScreenTiming timing : FullScreenTiming.values()) {
      fromStringMap.put(timing.value, timing);
    }
  }
}
