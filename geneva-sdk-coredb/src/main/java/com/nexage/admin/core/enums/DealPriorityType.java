package com.nexage.admin.core.enums;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;

@Getter
public enum DealPriorityType {
  OPEN(0);

  private final int priorityType;

  DealPriorityType(int priorityType) {
    this.priorityType = priorityType;
  }

  public int asInt() {
    return priorityType;
  }

  @Override
  public String toString() {
    return priorityType + " - " + name();
  }

  public static DealPriorityType fromInt(int value) {
    return fromIntMap.getOrDefault(value, null);
  }

  private static final Map<Integer, DealPriorityType> fromIntMap =
      Arrays.stream(values())
          .collect(toMap(DealPriorityType::getPriorityType, Function.identity()));
}
