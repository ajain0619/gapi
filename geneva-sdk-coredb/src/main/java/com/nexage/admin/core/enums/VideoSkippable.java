package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum VideoSkippable {
  NO(0),
  YES(1);

  private int value;

  VideoSkippable(int value) {
    this.value = value;
  }

  public static VideoSkippable fromInt(int value) {
    return fromIntMap.get(value);
  }

  private static final HashMap<Integer, VideoSkippable> fromIntMap = new HashMap<>();

  static {
    for (VideoSkippable protocol : VideoSkippable.values()) {
      fromIntMap.put(protocol.value, protocol);
    }
  }
}
