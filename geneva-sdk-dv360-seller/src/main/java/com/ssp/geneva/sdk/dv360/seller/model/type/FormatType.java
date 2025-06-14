package com.ssp.geneva.sdk.dv360.seller.model.type;

import java.util.Arrays;
import java.util.Optional;

public enum FormatType {
  DEAL_FORMAT_DISPLAY("DISPLAY"),
  DEAL_FORMAT_VIDEO("VIDEO"),
  DEAL_FORMAT_AUDIO("AUDIO");

  private String shortName;

  FormatType(String shortName) {
    this.shortName = shortName;
  }

  public String getShortName() {
    return shortName;
  }

  public boolean hasShortName(String shortName) {
    return this.shortName.equalsIgnoreCase(shortName);
  }

  public static boolean valid(String shortName) {
    return fromShortName(shortName).isPresent();
  }

  public static Optional<FormatType> fromShortName(String shortName) {
    return Arrays.stream(values()).filter(ft -> ft.hasShortName(shortName)).findAny();
  }

  public static String toShortName(FormatType formatType) {
    return formatType.shortName;
  }
}
