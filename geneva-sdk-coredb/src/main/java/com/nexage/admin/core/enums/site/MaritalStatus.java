package com.nexage.admin.core.enums.site;

import java.util.Map;

public enum MaritalStatus {
  Single("S"),
  Married("M"),
  Divorced("D"),
  Other("O");
  private final String code;

  MaritalStatus(String code) {
    this.code = code;
  }

  public String code() {
    return code;
  }

  public static final Map<String, MaritalStatus> defaultStringToMap =
      Map.of(
          MaritalStatus.Single.code(), MaritalStatus.Single,
          MaritalStatus.Married.code(), MaritalStatus.Married,
          MaritalStatus.Divorced.code(), MaritalStatus.Divorced,
          MaritalStatus.Other.code(), MaritalStatus.Other);
  public static final Map<MaritalStatus, String> defaultStringFromMap =
      Map.of(
          MaritalStatus.Single, MaritalStatus.Single.code(),
          MaritalStatus.Married, MaritalStatus.Married.code(),
          MaritalStatus.Divorced, MaritalStatus.Divorced.code(),
          MaritalStatus.Other, MaritalStatus.Other.code());
}
