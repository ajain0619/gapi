package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum VideoLinearity {
  LINEAR(1),
  NON_LINEAR(2);

  private int value;

  VideoLinearity(int value) {
    this.value = value;
  }

  public int asInt() {
    return this.value;
  }

  public static VideoLinearity fromInt(int value) {
    return fromIntMap.get(value);
  }

  private static final HashMap<Integer, VideoLinearity> fromIntMap = new HashMap<>();

  static {
    for (VideoLinearity method : VideoLinearity.values()) {
      fromIntMap.put(method.value, method);
    }
  }
}
