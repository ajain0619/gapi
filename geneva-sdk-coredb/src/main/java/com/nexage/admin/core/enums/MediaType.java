package com.nexage.admin.core.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MediaType {
  VIDEO_MP4("video/mp4");

  private final String value;

  private static final Map<String, MediaType> valuesMap = new HashMap<>();

  public static MediaType of(String value) {
    if (valuesMap.isEmpty()) {
      Arrays.stream(values()).forEach(item -> valuesMap.put(item.value, item));
    }
    if (!valuesMap.containsKey(value)) throw new IllegalArgumentException();
    return valuesMap.get(value);
  }
}
