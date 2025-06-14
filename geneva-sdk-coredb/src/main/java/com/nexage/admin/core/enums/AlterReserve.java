package com.nexage.admin.core.enums;

import java.util.HashMap;

/** @author vincentlawlor */
public enum AlterReserve {
  OFF(0), // Don't alter the reserve it's off
  ONLY_IF_HIGHER(1), // Alter reserve if AlephD provide a higher value
  ALWAYS(2); // Always use the reserve provided by AlephD

  private int alterReserveValue;

  private AlterReserve(int alterReserveValue) {
    this.alterReserveValue = alterReserveValue;
  }

  public int asInt() {
    return alterReserveValue;
  }

  private static final HashMap<Integer, AlterReserve> fromIntMap = new HashMap<>();

  static {
    for (AlterReserve s : AlterReserve.values()) {
      fromIntMap.put(s.asInt(), s);
    }
  }

  public static AlterReserve fromInt(Integer i) {
    return fromIntMap.get(i);
  }
}
