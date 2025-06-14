package com.nexage.admin.core.enums;

import java.util.Arrays;

public enum PlacementVideoLinearity implements HasInt<PlacementVideoLinearity> {
  LINEAR(1),
  NON_LINEAR(2);

  private int value;

  PlacementVideoLinearity(int value) {
    this.value = value;
  }

  @Override
  public int asInt() {
    return this.value;
  }

  @Override
  public PlacementVideoLinearity fromInt(int value) {
    return Arrays.stream(values()).filter(v -> v.value == value).findFirst().orElse(null);
  }
}
