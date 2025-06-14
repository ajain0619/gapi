package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum TierType {
  WATERFALL(0),
  SUPER_AUCTION(1),
  SY_DECISION_MAKER(2);

  int code;

  private TierType(int code) {
    this.code = code;
  }

  public int asInt() {
    return code;
  }

  private static final HashMap<Integer, TierType> fromIntMap = new HashMap<>();

  static {
    for (TierType s : TierType.values()) {
      fromIntMap.put(s.asInt(), s);
    }
  }

  public static TierType fromInt(Integer i) {
    return fromIntMap.get(i);
  }
}
