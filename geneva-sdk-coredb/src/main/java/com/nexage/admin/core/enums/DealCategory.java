package com.nexage.admin.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;

public enum DealCategory {
  SSP(1, "SSP"),
  S2S_PLACEMENT_DEAL(4, "S2S Placement Deal");

  private final int val;
  private final String name;

  DealCategory(int val, String name) {
    this.val = val;
    this.name = name;
  }

  public int asInt() {
    return this.val;
  }

  public static DealCategory fromInt(int val) {
    return fromIntMap.get(val);
  }

  @JsonValue
  public String getName() {
    return name;
  }

  private static final HashMap<Integer, DealCategory> fromIntMap = new HashMap<>();

  static {
    for (DealCategory at : DealCategory.values()) {
      fromIntMap.put(at.val, at);
    }
  }
}
