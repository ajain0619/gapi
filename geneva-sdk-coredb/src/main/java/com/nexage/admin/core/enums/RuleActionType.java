package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum RuleActionType implements HasInt<RuleActionType> {
  FLOOR(0),
  FILTER(1),
  VISIBILITY(2);

  private int type;

  RuleActionType(int type) {
    this.type = type;
  }

  @Override
  public int asInt() {
    return type;
  }

  @Override
  public RuleActionType fromInt(int i) {
    return fromIntMap.get(i);
  }

  private static final HashMap<Integer, RuleActionType> fromIntMap = new HashMap<>();

  static {
    for (RuleActionType s : RuleActionType.values()) {
      fromIntMap.put(s.asInt(), s);
    }
  }
}
