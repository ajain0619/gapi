package com.nexage.admin.core.enums;

import java.util.Arrays;

public enum PlacementVideoStreamType implements HasInt<PlacementVideoStreamType> {
  VOD(0),
  LIVE(1);

  private int value;

  PlacementVideoStreamType(int value) {
    this.value = value;
  }

  @Override
  public int asInt() {
    return this.value;
  }

  @Override
  public PlacementVideoStreamType fromInt(int value) {
    return Arrays.stream(values()).filter(v -> v.value == value).findFirst().orElse(null);
  }
}
