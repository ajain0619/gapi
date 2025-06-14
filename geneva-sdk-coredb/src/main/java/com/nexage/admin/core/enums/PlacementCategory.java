package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum PlacementCategory {
  BANNER(0),
  INTERSTITIAL(1),
  MEDIUM_RECTANGLE(2),
  NATIVE(3),
  INSTREAM_VIDEO(4),
  REWARDED_VIDEO(5),
  NATIVE_V2(6),
  IN_ARTICLE(7),
  IN_FEED(8);

  private int value;

  PlacementCategory(int value) {
    this.value = value;
  }

  public int asInt() {
    return this.value;
  }

  public static PlacementCategory fromInt(int value) {
    return fromIntMap.get(value);
  }

  private static final HashMap<Integer, PlacementCategory> fromIntMap = new HashMap<>();

  static {
    for (PlacementCategory protocol : PlacementCategory.values()) {
      fromIntMap.put(protocol.value, protocol);
    }
  }
}
