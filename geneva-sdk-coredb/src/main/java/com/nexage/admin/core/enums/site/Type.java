package com.nexage.admin.core.enums.site;

import java.util.HashMap;
import java.util.Map;

public enum Type {
  MOBILE_WEB,
  APPLICATION,
  DESKTOP,
  DOOH,
  WEBSITE;

  private static final Map<String, Type> STRING_TO_TYPE = new HashMap<>();

  static {
    for (Type type : values()) {
      STRING_TO_TYPE.put(type.name(), type);
    }
  }

  public static Type fromString(String asString) {
    return STRING_TO_TYPE.get(asString);
  }
}
