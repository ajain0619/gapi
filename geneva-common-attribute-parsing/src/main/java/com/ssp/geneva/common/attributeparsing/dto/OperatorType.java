package com.ssp.geneva.common.attributeparsing.dto;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum OperatorType {
  IN("IN"),
  NOT_IN("NOT IN");

  String operator;

  OperatorType(String operator) {
    this.operator = operator;
  }

  @Override
  public String toString() {
    return this.operator;
  }

  static Function<OperatorType, OperatorType> identityFunction = Function.identity();

  private static Map<String, OperatorType> map =
      Arrays.stream(OperatorType.values())
          .collect(Collectors.toMap(OperatorType::getOperator, identityFunction));

  public String getOperator() {
    return operator;
  }

  public static OperatorType of(String operator) {
    return map.get(operator);
  }
}
