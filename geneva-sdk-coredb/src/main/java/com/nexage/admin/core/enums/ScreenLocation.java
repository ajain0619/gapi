package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum ScreenLocation {
  UNKNOWN(0),
  ABOVE_VISIBLE(1),
  BELOW_VISIBLE(3),
  HEADER_VISIBLE(4),
  FOOTER_VISIBLE(5),
  SIDEBAR_VISIBLE(6),
  FULLSCREEN_VISIBLE(7),
  UNDEFINED(-1);

  private final int value;

  ScreenLocation(int value) {
    this.value = value;
  }

  public int asInt() {
    return value;
  }

  public static ScreenLocation fromInt(int value) {
    return fromIntMap.get(value);
  }

  private static final HashMap<Integer, ScreenLocation> fromIntMap = new HashMap<>();

  static {
    for (ScreenLocation pos : ScreenLocation.values()) {
      fromIntMap.put(pos.value, pos);
    }
  }
}
