package com.ssp.geneva.sdk.messaging.config;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class MessagingSdkProperties {

  private String messagingPrefix;
  private int retryAttempts;
  private String storeKey;
  private String metricsPrefix;

  public String getAdjustedStoreKey() {
    return messagingPrefix + "_" + storeKey;
  }
}
