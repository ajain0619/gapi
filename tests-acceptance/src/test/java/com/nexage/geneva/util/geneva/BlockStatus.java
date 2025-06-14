package com.nexage.geneva.util.geneva;

public enum BlockStatus {
  ACTIVE("Active"),
  INACTIVE("Inactive");

  private String name;

  BlockStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static BlockStatus getBlockStatus(String name) {
    BlockStatus result = null;

    for (BlockStatus blockStatus : values()) {
      if (blockStatus.getName().equals(name)) {
        result = blockStatus;
        break;
      }
    }

    return result;
  }
}
