package com.nexage.admin.core.enums;

import java.util.HashMap;

/** Defines various status codes used in the Mediation platform */
public enum Status {
  DELETED(-1),
  INACTIVE(0),
  ACTIVE(1);

  int statusValue;

  Status(int statusValue) {
    this.statusValue = statusValue;
  }

  public int asInt() {
    return statusValue;
  }

  private static final HashMap<Integer, Status> fromIntMap = new HashMap<>();

  static {
    for (Status s : Status.values()) {
      fromIntMap.put(s.asInt(), s);
    }
  }

  public static Status fromInt(int i) {
    return fromIntMap.get(i);
  }
}
