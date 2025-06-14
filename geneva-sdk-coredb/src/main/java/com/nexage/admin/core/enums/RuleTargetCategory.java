package com.nexage.admin.core.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RuleTargetCategory {
  SUPPLY(0),
  BUYER(1),
  BID(2);

  // Order of data availability in an auction context (supply -> buyer -> bid response)
  private final int order;

  RuleTargetCategory(int order) {
    this.order = order;
  }

  public int getOrder() {
    return order;
  }

  public static RuleTargetCategory fromOrder(int order) {
    return fromOrderMap.get(order);
  }

  private static final Map<Integer, RuleTargetCategory> fromOrderMap =
      Stream.of(values())
          .collect(Collectors.toMap(RuleTargetCategory::getOrder, Function.identity()));
}
