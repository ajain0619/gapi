package com.nexage.admin.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** This class is similar to the Guava Splitter.MapSplitter but allows for spaces before the ke */
public class MapSplitter {

  private String keyValueSeparator = "=";
  private String separator = ",";

  private MapSplitter() {}

  public static MapSplitter separator(String separator) {
    MapSplitter splitter = new MapSplitter();
    splitter.separator = separator;

    return splitter;
  }

  public MapSplitter withKeyValueSeparator(String keyValueSeparator) {
    this.keyValueSeparator = keyValueSeparator;

    return this;
  }

  public Map<String, String> split(String nameValues) {
    Map<String, String> nameValueMap = new TreeMap<>();
    if (nameValues != null && !"".equals(nameValues)) {
      String[] pairs = nameValues.split(separator);
      for (String pair : pairs) {
        String[] nv = pair.trim().split(keyValueSeparator);
        nameValueMap.put(nv[0].trim(), nv.length > 1 ? nv[1].trim() : "");
      }
    }
    return nameValueMap;
  }

  /**
   * Split input string into map with key of type Long.
   *
   * @param nameValues key-value input string
   * @return map with key of type Long and value type String
   */
  public Map<Long, String> splitAsLong(String nameValues) {
    var nameValueMap = new HashMap<Long, String>();
    if (nameValues != null && !"".equals(nameValues)) {
      String[] pairs = nameValues.split(separator);
      for (String pair : pairs) {
        String[] nv = pair.trim().split(keyValueSeparator);
        nameValueMap.put(Long.parseLong(nv[0].trim()), nv.length > 1 ? nv[1].trim() : "");
      }
    }
    return nameValueMap;
  }
}
