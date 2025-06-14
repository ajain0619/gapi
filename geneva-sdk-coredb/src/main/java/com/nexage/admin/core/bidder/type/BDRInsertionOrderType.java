package com.nexage.admin.core.bidder.type;

import java.util.HashMap;

public enum BDRInsertionOrderType {
  NEXAGE(0),
  OTHER(1);

  private int value;

  private BDRInsertionOrderType(int value) {
    this.value = value;
  }

  public static BDRInsertionOrderType fromInt(int value) {
    return fromIntMap.get(value);
  }

  public int asInt() {
    return value;
  }

  private static final HashMap<Integer, BDRInsertionOrderType> fromIntMap = new HashMap<>();

  static {
    for (BDRInsertionOrderType status : BDRInsertionOrderType.values()) {
      fromIntMap.put(status.value, status);
    }
  }
}
