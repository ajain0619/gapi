package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum Mode {
  TEST(0),
  LIVE(1);

  private int value;

  Mode(int value) {
    this.value = value;
  }

  public int asInt() {
    return this.value;
  }

  public static Mode fromInt(int value) {
    return fromIntMap.get(value);
  }

  private static final HashMap<Integer, Mode> fromIntMap = new HashMap<>();

  static {
    for (Mode protocol : Mode.values()) {
      fromIntMap.put(protocol.value, protocol);
    }
  }
}
