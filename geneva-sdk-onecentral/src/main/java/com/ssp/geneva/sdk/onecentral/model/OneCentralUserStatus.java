package com.ssp.geneva.sdk.onecentral.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OneCentralUserStatus {
  ACTIVE("ACTIVE"),
  INACTIVE("INACTIVE"),
  DELETED("DELETED"),
  PASSWORD_RESET("Password Reset"),
  PENDING_ACTIVATION("Pending Activation");

  private final String value;

  OneCentralUserStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
