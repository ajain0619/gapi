package com.nexage.admin.core.enums;

public enum Feature {
  FEATURE_WAP(1),
  FEATURE_WEB(2),
  FEATURE_ADMAX(4),
  FEATURE_ANALYTICS(8);

  private int code;

  Feature(int code) {
    this.code = code;
  }

  public int getValue() {
    return code;
  }

  public static Feature getStatus(int code) {
    switch (code) {
      case 1:
        return FEATURE_WAP;
      case 2:
        return FEATURE_WEB;
      case 4:
        return FEATURE_ADMAX;
      case 8:
        return FEATURE_ANALYTICS;
      default:
        return null;
    }
  }
}
