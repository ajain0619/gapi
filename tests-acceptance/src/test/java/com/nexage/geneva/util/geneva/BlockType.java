package com.nexage.geneva.util.geneva;

public enum BlockType {
  CAMPAIGN_ID("Campaign"),
  CREATIVE_ID("Creative"),
  SEAT_ID("Seat"),
  ADOMAIN("Advertiser Domain");

  private String name;

  BlockType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static BlockType getBlockType(String name) {
    BlockType result = null;

    for (BlockType blockType : values()) {
      if (blockType.getName().equals(name)) {
        result = blockType;
        break;
      }
    }

    return result;
  }
}
