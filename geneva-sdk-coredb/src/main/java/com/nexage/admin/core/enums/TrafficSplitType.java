package com.nexage.admin.core.enums;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;

@Getter
public enum TrafficSplitType implements HasInt<TrafficSplitType> {
  EVALUATE(0),
  SSP_COIN_TOSS(1);

  private int type;

  TrafficSplitType(int type) {
    this.type = type;
  }

  public int asInt() {
    return this.type;
  }

  /**
   * This method will take the value provided , go into the other from method and test if the
   * integer value matches with any values in the {@link TrafficSplitType} class, if the return is
   * null, this function will return the specified default value EVALUATE.
   *
   * @param i
   * @return the {@link TrafficSplitType} corresponding to the value, or the default otherwise
   */
  public TrafficSplitType fromInt(int i) {
    return fromIntMap.getOrDefault(i, EVALUATE);
  }

  private static final Map<Integer, TrafficSplitType> fromIntMap =
      Arrays.stream(values()).collect(toMap(TrafficSplitType::getType, Function.identity()));
}
