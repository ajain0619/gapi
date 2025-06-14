package com.nexage.admin.core.enums;

public enum ChannelType {
  CHANNEL_TYPE_PUBLISHER(100), // A publisher channel
  CHANNEL_TYPE_OPERATOR(101), // An operator channel
  CHANNEL_TYPE_APP_STORE(102); // An App store channel

  private int code;

  ChannelType(int code) {
    this.code = code;
  }

  public int getValue() {
    return code;
  }

  public static ChannelType getStatus(int code) {
    switch (code) {
      case 100:
        return CHANNEL_TYPE_PUBLISHER;
      case 101:
        return CHANNEL_TYPE_OPERATOR;
      case 102:
        return CHANNEL_TYPE_APP_STORE;
      default:
        return null;
    }
  }
}
