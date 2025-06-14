package com.ssp.geneva.common.model.config;

public enum FilterAuction {
  ALL(0),
  FIRST_PRICE(1),
  SECOND_PRICE_PLUS(2);

  private final int value;

  FilterAuction(int value) {
    this.value = value;
  }

  public static boolean isValidValue(int auctionType) {
    return auctionType >= FilterAuction.ALL.getValue()
        && auctionType <= FilterAuction.SECOND_PRICE_PLUS.getValue();
  }

  public int getValue() {
    return value;
  }
}
