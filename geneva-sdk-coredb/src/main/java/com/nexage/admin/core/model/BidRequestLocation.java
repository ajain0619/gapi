package com.nexage.admin.core.model;

public enum BidRequestLocation {
  SiteApp(0),
  Device(1),
  User(2);

  private final int externalValue;

  BidRequestLocation(int externalValue) {
    this.externalValue = externalValue;
  }

  public int getExternalValue() {
    return externalValue;
  }
}
