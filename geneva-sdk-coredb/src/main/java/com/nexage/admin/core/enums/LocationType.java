package com.nexage.admin.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LocationType {
  INDOOR("Indoor"),
  OUTDOOR("Outdoor"),
  MOVING("Moving");
  private final String name;

  LocationType(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return this.name;
  }
}
