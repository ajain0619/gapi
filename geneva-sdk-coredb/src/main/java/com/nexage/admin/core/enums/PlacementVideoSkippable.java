package com.nexage.admin.core.enums;

import java.util.Arrays;

public enum PlacementVideoSkippable implements HasInt<PlacementVideoSkippable> {
  NO(0),
  YES(1);

  private int value;

  PlacementVideoSkippable(int value) {
    this.value = value;
  }

  @Override
  public int asInt() {
    return this.value;
  }

  @Override
  public PlacementVideoSkippable fromInt(int value) {
    return Arrays.stream(values()).filter(v -> v.value == value).findFirst().orElse(null);
  }
}
