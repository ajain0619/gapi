package com.nexage.admin.core.enums;

import java.util.HashMap;

/** Defines various billing sources used in the Mediation platform */
public enum BillingSource {
  ONE_MOBILE(1),
  BRXD(2);

  int billingSourceValue;

  BillingSource(int billingSourceValue) {
    this.billingSourceValue = billingSourceValue;
  }

  public int asInt() {
    return billingSourceValue;
  }

  private static final HashMap<Integer, BillingSource> fromIntMap = new HashMap<>();

  static {
    for (BillingSource s : BillingSource.values()) {
      fromIntMap.put(s.asInt(), s);
    }
  }

  public static BillingSource fromInt(int i) {
    return fromIntMap.get(i);
  }
}
