package com.nexage.admin.core.enums;

import java.util.HashMap;
import java.util.Map;

public enum ContentEncodingType {
  GZIP;

  private static final Map<String, ContentEncodingType> STRING_TO_TYPE_MAP = new HashMap<>();

  static {
    for (ContentEncodingType type : values()) {
      STRING_TO_TYPE_MAP.put(type.name(), type);
    }
  }

  public static ContentEncodingType fromString(String asString) {
    return STRING_TO_TYPE_MAP.get(asString);
  }
}
