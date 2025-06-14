package com.nexage.admin.core.bidder.type;

import java.util.HashMap;

public enum BDRTargetDay {
  ALL(0),
  SUN(1),
  MON(2),
  TUE(3),
  WED(4),
  THURS(5),
  FRI(6),
  SAT(7);

  private int value;

  private BDRTargetDay(int value) {
    this.value = value;
  }

  public static BDRTargetDay fromInt(int value) {
    return fromIntMap.get(value);
  }

  public int asInt() {
    return value;
  }

  private static final HashMap<Integer, BDRTargetDay> fromIntMap = new HashMap<>();

  static {
    for (BDRTargetDay target : BDRTargetDay.values()) {
      fromIntMap.put(target.value, target);
    }
  }

  public static boolean isValid(int value) {
    return fromIntMap.containsKey(value);
  }
}
