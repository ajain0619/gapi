package com.nexage.admin.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;

public enum BlockListInclusion {
  NO(0),
  YES(1),
  ADAPTIVE(2);

  private int value;

  BlockListInclusion(int value) {
    this.value = value;
  }

  @JsonValue
  public int getValue() {
    return value;
  }

  public static BlockListInclusion fromInt(int i) {
    return fromBlockListInclusionMap.get(i);
  }

  private static final HashMap<Integer, BlockListInclusion> fromBlockListInclusionMap =
      new HashMap<>();

  static {
    for (BlockListInclusion b : BlockListInclusion.values()) {
      fromBlockListInclusionMap.put(b.value, b);
    }
  }
}
