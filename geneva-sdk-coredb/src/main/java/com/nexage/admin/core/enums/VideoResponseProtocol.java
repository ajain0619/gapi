package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum VideoResponseProtocol {
  VAST_1(1),
  VAST_2(2),
  VAST_3(3),
  VAST_1_WRAPPER(4),
  VAST_2_WRAPPER(5),
  VAST_3_WRAPPER(6);

  private int value;

  VideoResponseProtocol(int value) {
    this.value = value;
  }

  public int asInt() {
    return this.value;
  }

  public static VideoResponseProtocol fromInt(int value) {
    return fromIntMap.get(value);
  }

  private static final HashMap<Integer, VideoResponseProtocol> fromIntMap = new HashMap<>();

  static {
    for (VideoResponseProtocol protocol : VideoResponseProtocol.values()) {
      fromIntMap.put(protocol.value, protocol);
    }
  }
}
