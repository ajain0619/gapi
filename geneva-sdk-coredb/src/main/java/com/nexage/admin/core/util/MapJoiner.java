package com.nexage.admin.core.util;

import java.util.HashMap;
import java.util.Map;

public class MapJoiner {

  private String keyValueSeparator = "=";
  private String separator = ",";
  private boolean excludeNullValues = false;

  private MapJoiner() {}

  public static MapJoiner separator(String separator) {
    MapJoiner joiner = new MapJoiner();
    joiner.separator = separator;

    return joiner;
  }

  public MapJoiner withKeyValueSeparator(String keyValueSeparator) {
    this.keyValueSeparator = keyValueSeparator;

    return this;
  }

  public MapJoiner excludeNullValues(boolean excludeNullValues) {
    this.excludeNullValues = excludeNullValues;

    return this;
  }

  public String join(Map<String, String> map) {
    StringBuilder sb = new StringBuilder();

    for (String key : map.keySet()) {
      String value = map.get(key);

      if (sb.length() > 0) sb.append(separator);
      sb.append(key);
      if (excludeNullValues && value == null) {

      } else {
        sb.append(keyValueSeparator);
        sb.append(value);
      }
    }
    return sb.toString();
  }

  public Map<String, String> split(String nameValues) {
    Map<String, String> nameValueMap = new HashMap<>();
    if (nameValues != null && !"".equals(nameValues)) {
      String[] pairs = nameValues.split(separator);
      for (String pair : pairs) {
        String[] nv = pair.trim().split(keyValueSeparator);
        nameValueMap.put(nv[0].trim(), nv.length > 1 ? nv[1].trim() : null);
      }
    }
    return nameValueMap;
  }
}
