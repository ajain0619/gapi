package com.ssp.geneva.common.model.config;

public enum BidUnit {
  USD_CPM(0),
  USD_PER_UNIT(1),
  USD_MICROS_PER_UNIT(2);

  private final int value;

  BidUnit(int value) {
    this.value = value;
  }

  public static boolean isValidValue(int bidUnit) {
    return bidUnit >= BidUnit.USD_CPM.getValue()
        && bidUnit <= BidUnit.USD_MICROS_PER_UNIT.getValue();
  }

  public int getValue() {
    return value;
  }
}
