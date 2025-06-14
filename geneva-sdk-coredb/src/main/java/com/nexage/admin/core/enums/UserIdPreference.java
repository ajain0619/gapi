package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum UserIdPreference {
  NO_ID_RESTRICTION(0),
  DEVICE_ID(1),
  EXCHANGE_USER_ID_OR_DEVICE_ID(2),
  MATCHED_USER_ID_OR_DEVICE_ID(3),
  MATCHED_USER_ID(4);

  private int id;

  UserIdPreference(int value) {
    this.id = value;
  }

  public int getId() {
    return this.id;
  }

  public static UserIdPreference fromInt(int value) {
    return fromIntMap.get(value);
  }

  private static final HashMap<Integer, UserIdPreference> fromIntMap = new HashMap<>();

  static {
    for (UserIdPreference method : UserIdPreference.values()) {
      fromIntMap.put(method.id, method);
    }
  }
}
