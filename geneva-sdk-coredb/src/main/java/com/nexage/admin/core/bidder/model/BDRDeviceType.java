package com.nexage.admin.core.bidder.model;

import java.util.HashMap;

public enum BDRDeviceType {
  TABLET(1),
  SMARTPHONE(2);

  private int externalValue;

  private BDRDeviceType(int externalValue) {
    this.externalValue = externalValue;
  }

  public int asInt() {
    return externalValue;
  }

  private static final HashMap<Integer, BDRDeviceType> fromIntMap = new HashMap<>();

  static {
    for (BDRDeviceType s : BDRDeviceType.values()) {
      fromIntMap.put(s.asInt(), s);
    }
  }

  public static BDRDeviceType fromInt(Integer i) {
    return fromIntMap.get(i);
  }

  public static boolean isValid(int externalValue) {
    return fromIntMap.containsKey(externalValue);
  }
}
