package com.nexage.app.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ramswaroop
 * @version 21/02/2017
 */
public enum LimitItem {
  SITES("sites"),
  POSITIONS_PER_SITE("positions_per_site"),
  TAGS_PER_POSITION("tags_per_position"),
  CAMPAIGNS("campaigns"),
  CREATIVES_PER_CAMPAIGN("creatives_per_campaign"),
  BIDDER_LIBRARIES("bidder_libraries"),
  BLOCK_LIBRARIES("block_libraries"),
  USERS("users");

  private final String name;

  private LimitItem(String name) {
    this.name = name;
  }

  private static Map<String, LimitItem> nameToEnumMap = new HashMap<>();

  static {
    for (LimitItem item : LimitItem.values()) {
      nameToEnumMap.put(item.name, item);
    }
  }

  public static LimitItem getEnum(String name) {
    return nameToEnumMap.get(name);
  }
}
