package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum RuleType implements HasInt<RuleType> {
  BRAND_PROTECTION(1),
  DEAL(2),
  EXPERIMENTATION(4);

  private int type;

  RuleType(int type) {
    this.type = type;
  }

  public int asInt() {
    return type;
  }

  public RuleType fromInt(int i) {
    return fromIntMap.get(i);
  }

  private static final HashMap<Integer, RuleType> fromIntMap = new HashMap<>();

  static {
    for (RuleType s : RuleType.values()) {
      fromIntMap.put(s.asInt(), s);
    }
  }
}
