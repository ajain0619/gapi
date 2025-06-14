package com.nexage.admin.core.enums;

import java.util.Arrays;

public enum PlacementVideoVastVersion implements HasInt<PlacementVideoVastVersion> {
  VAST2_0(2),
  VAST3_0(3);

  private int value;

  PlacementVideoVastVersion(int value) {
    this.value = value;
  }

  @Override
  public int asInt() {
    return this.value;
  }

  @Override
  public PlacementVideoVastVersion fromInt(int value) {
    return Arrays.stream(values()).filter(v -> v.value == value).findFirst().orElse(null);
  }
}
