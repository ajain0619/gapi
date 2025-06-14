package com.nexage.admin.core.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AssociationType {
  NON_DEFAULT(0),
  DEFAULT(1),
  DEFAULT_BANNER(2),
  DEFAULT_VIDEO(3);

  @Getter private final int value;

  public static AssociationType getFromValue(Integer type) {
    return (type == null || fromIntMap.get(type) == null) ? defaultType() : fromIntMap.get(type);
  }

  public static AssociationType defaultType() {
    return NON_DEFAULT;
  }

  private static final Map<Integer, AssociationType> fromIntMap =
      Stream.of(values()).collect(Collectors.toMap(AssociationType::getValue, Function.identity()));
}
