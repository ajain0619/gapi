package com.nexage.admin.core.bidder.type;

import java.util.HashMap;

public enum BDRStatus {
  DELETED(-1),
  INACTIVE(0),
  ACTIVE(1),
  DISCOVERY(2);

  private int value;

  private BDRStatus(int value) {
    this.value = value;
  }

  public static BDRStatus fromInt(int value) {
    return fromIntMap.get(value);
  }

  public int asInt() {
    return value;
  }

  private static final HashMap<Integer, BDRStatus> fromIntMap = new HashMap<>();

  static {
    for (BDRStatus status : BDRStatus.values()) {
      fromIntMap.put(status.value, status);
    }
  }
}
