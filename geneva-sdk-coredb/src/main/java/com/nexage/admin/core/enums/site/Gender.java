package com.nexage.admin.core.enums.site;

import java.util.Map;

public enum Gender {
  Male("M"),
  Female("F"),
  Other("O");
  private final String code;

  Gender(String code) {
    this.code = code;
  }

  public String code() {
    return code;
  }

  public static final Map<String, Gender> defaultStringToMap =
      Map.of(
          Gender.Male.code(), Gender.Male,
          Gender.Female.code(), Gender.Female,
          Gender.Other.code(), Gender.Other);
  public static final Map<Gender, String> defaultStringFromMap =
      Map.of(
          Gender.Male, Gender.Male.code(),
          Gender.Female, Gender.Female.code(),
          Gender.Other, Gender.Other.code());
}
