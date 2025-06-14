package com.nexage.admin.core.bidder.type;

import java.util.HashMap;

public enum BDRMraidCompliance {
  NONE(-1),
  VERSION1(1),
  VERSION2(2);

  private int value;

  private BDRMraidCompliance(int value) {
    this.value = value;
  }

  public static BDRMraidCompliance fromInt(int value) {
    return fromIntMap.get(value);
  }

  public int asInt() {
    return value;
  }

  private static final HashMap<Integer, BDRMraidCompliance> fromIntMap = new HashMap<>();

  static {
    for (BDRMraidCompliance status : BDRMraidCompliance.values()) {
      fromIntMap.put(status.value, status);
    }
  }
}
