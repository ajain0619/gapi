package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum ImpressionTypeHandling {
  BASED_ON_PLACEMENT_CONFIG(0),
  BASED_ON_INBOUND_REQUEST(1);

  private int value;

  ImpressionTypeHandling(int value) {
    this.value = value;
  }

  public static ImpressionTypeHandling fromInt(int value) {
    return fromIntMap.get(value);
  }

  private static final HashMap<Integer, ImpressionTypeHandling> fromIntMap = new HashMap<>();

  static {
    for (ImpressionTypeHandling protocol : ImpressionTypeHandling.values()) {
      fromIntMap.put(protocol.value, protocol);
    }
  }
}
