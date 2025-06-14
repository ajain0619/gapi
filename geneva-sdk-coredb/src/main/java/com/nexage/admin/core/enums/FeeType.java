package com.nexage.admin.core.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FeeType implements HasInt<FeeType> {
  PERCENTAGE(0);

  private int type;

  FeeType(int type) {
    this.type = type;
  }

  public int asInt() {
    return type;
  }

  public FeeType fromInt(int i) {
    return fromIntMap.get(i);
  }

  private static final Map<Integer, FeeType> fromIntMap =
      Stream.of(values()).collect(Collectors.toMap(FeeType::asInt, Function.identity()));
}
