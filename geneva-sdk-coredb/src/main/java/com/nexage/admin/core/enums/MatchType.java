package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum MatchType {
  EXCLUDE_LIST(0),
  INCLUDE_LIST(1);

  private int type;

  MatchType(int type) {
    this.type = type;
  }

  public int asInt() {
    return type;
  }

  private static final HashMap<Integer, MatchType> fromIntMap = new HashMap<>();

  static {
    for (MatchType s : MatchType.values()) {
      fromIntMap.put(s.asInt(), s);
    }
  }

  public static MatchType fromInt(int i) {
    return fromIntMap.get(i);
  }
}
