package com.nexage.admin.core.enums.site;

import java.util.Map;

public enum Ethnicity {
  African_American("0"),
  Asian("1"),
  Hispanic("2"),
  White("3"),
  Other("4");

  private final String code;

  Ethnicity(String code) {
    this.code = code;
  }

  public String code() {
    return code;
  }

  public static final Map<String, Ethnicity> defaultStringToMap =
      Map.of(
          Ethnicity.African_American.code(),
          Ethnicity.African_American,
          Ethnicity.Asian.code(),
          Ethnicity.Asian,
          Ethnicity.Hispanic.code(),
          Ethnicity.Hispanic,
          Ethnicity.White.code(),
          Ethnicity.White,
          Ethnicity.Other.code(),
          Ethnicity.Other);
  public static final Map<Ethnicity, String> defaultStringFromMap =
      Map.of(
          Ethnicity.African_American,
          Ethnicity.African_American.code(),
          Ethnicity.Asian,
          Ethnicity.Asian.code(),
          Ethnicity.Hispanic,
          Ethnicity.Hispanic.code(),
          Ethnicity.White,
          Ethnicity.White.code(),
          Ethnicity.Other,
          Ethnicity.Other.code());
}
