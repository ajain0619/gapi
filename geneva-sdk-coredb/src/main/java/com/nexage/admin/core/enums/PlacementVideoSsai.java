package com.nexage.admin.core.enums;

import java.util.Arrays;

public enum PlacementVideoSsai implements HasInt<PlacementVideoSsai> {
  UNKNOWN(0),
  ALL_CLIENT_SIDE(1),
  ASSETS_STICHED_SERVER_SIDE(2),
  ALL_SERVER_SIDE(3);

  private int value;

  PlacementVideoSsai(int value) {
    this.value = value;
  }

  @Override
  public int asInt() {
    return this.value;
  }

  @Override
  public PlacementVideoSsai fromInt(int value) {
    return Arrays.stream(values()).filter(v -> v.value == value).findFirst().orElse(null);
  }
}
