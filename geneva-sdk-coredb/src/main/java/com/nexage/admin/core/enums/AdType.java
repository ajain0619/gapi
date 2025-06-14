package com.nexage.admin.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum AdType {
  IMAGE("Image"),
  SILENT_VIDEO("Silent Video"),
  SOUND_ON_VIDEO("Sound-on Video");

  private static final Map<String, AdType> names =
      Arrays.stream(AdType.values())
          .collect(Collectors.toMap(AdType::getName, Function.identity()));
  private final String name;

  AdType(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return this.name;
  }

  public static AdType getValueFromName(String name) {
    return names.get(name);
  }
}
