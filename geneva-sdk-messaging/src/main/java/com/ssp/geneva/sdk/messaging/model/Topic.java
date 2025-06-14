package com.ssp.geneva.sdk.messaging.model;

import lombok.Getter;

@Getter
public enum Topic {
  PLACEMENT("placement"),
  COMPANY("company");

  private String name;

  Topic(String name) {
    this.name = name;
  }
}
