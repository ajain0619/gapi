package com.nexage.admin.core.enums;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;

@Getter
public enum AdSizeType {
  DYNAMIC(0),
  STANDARD(1),
  CUSTOM(2);

  private int code;

  AdSizeType(int code) {
    this.code = code;
  }

  private static final Map<Integer, AdSizeType> fromIntMap =
      Arrays.stream(values()).collect(toMap(AdSizeType::getCode, Function.identity()));

  public static AdSizeType fromCode(Integer i) {
    return fromIntMap.get(i);
  }
}
