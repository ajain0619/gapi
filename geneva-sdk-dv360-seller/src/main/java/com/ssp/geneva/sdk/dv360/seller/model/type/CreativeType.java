package com.ssp.geneva.sdk.dv360.seller.model.type;

import java.util.Arrays;
import java.util.Optional;

public enum CreativeType {
  CREATIVE_TYPE_UNSPECIFIED("UNSPECIFIED"),
  CREATIVE_TYPE_DISPLAY("DISPLAY"),
  CREATIVE_TYPE_VIDEO("VIDEO"),
  CREATIVE_TYPE_AUDIO("AUDIO"),
  CREATIVE_TYPE_NATIVE("NATIVE");

  private String shortName;

  CreativeType(String shortName) {
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

  public static Optional<CreativeType> fromShortName(String shortName) {
    return Arrays.stream(values()).filter(ft -> ft.hasShortName(shortName)).findFirst();
  }

  public static String toShortName(CreativeType creativeType) {
    return creativeType.shortName;
  }
}
