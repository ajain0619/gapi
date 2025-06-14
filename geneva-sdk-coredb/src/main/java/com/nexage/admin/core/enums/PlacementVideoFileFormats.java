package com.nexage.admin.core.enums;

import java.util.Arrays;

public enum PlacementVideoFileFormats implements HasInt<PlacementVideoFileFormats> {
  MPEG4(0),
  HLS(1),
  WEBM(2),
  WINDOWS_MEDIA(3);

  private int value;

  PlacementVideoFileFormats(int value) {
    this.value = value;
  }

  @Override
  public int asInt() {
    return this.value;
  }

  @Override
  public PlacementVideoFileFormats fromInt(int value) {
    return Arrays.stream(values()).filter(v -> v.value == value).findFirst().orElse(null);
  }
}
