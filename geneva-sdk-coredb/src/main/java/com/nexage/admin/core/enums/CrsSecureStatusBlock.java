package com.nexage.admin.core.enums;

import java.util.HashMap;

public enum CrsSecureStatusBlock {
  ALLOW_ALL(0),
  ALLOW_SECURE_OR_PARTIAL_ONLY(1),
  ALLOW_SECURE_ONLY(2);

  private final int code;

  CrsSecureStatusBlock(int code) {
    this.code = code;
  }

  public int asInt() {
    return code;
  }

  private static final HashMap<Integer, CrsSecureStatusBlock> fromIntMap = new HashMap<>();

  static {
    for (CrsSecureStatusBlock sslStatus : CrsSecureStatusBlock.values()) {
      fromIntMap.put(sslStatus.asInt(), sslStatus);
    }
  }

  public static CrsSecureStatusBlock fromInt(Integer code) {
    return fromIntMap.get(code);
  }
}
