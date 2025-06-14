package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum TrafficType {
  MEDIATION(0),
  SMART_YIELD(1);

  private int value;

  TrafficType(int value) {
    this.value = value;
  }

  public int asInt() {
    return this.value;
  }

  public static TrafficType fromInt(int value) {
    return fromIntMap.get(value);
  }

  private static final HashMap<Integer, TrafficType> fromIntMap = new HashMap<>();

  static {
    for (TrafficType protocol : TrafficType.values()) {
      fromIntMap.put(protocol.value, protocol);
    }
  }
}
