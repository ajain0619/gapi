package com.nexage.app.dto.transparency;

import java.util.HashMap;

public enum TransparencyMode {
  None(0),
  RealName(1),
  Aliases(2);

  int value;

  TransparencyMode(int val) {
    this.value = val;
  }

  public int asInt() {
    return value;
  }

  private static final HashMap<Integer, TransparencyMode> MAPPINGS = new HashMap<>();

  static {
    for (TransparencyMode s : TransparencyMode.values()) {
      MAPPINGS.put(s.asInt(), s);
    }
  }

  public static TransparencyMode fromInt(Integer i) {
    return MAPPINGS.get(i);
  }
}
