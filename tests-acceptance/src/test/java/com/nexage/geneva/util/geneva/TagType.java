package com.nexage.geneva.util.geneva;

/** Enum represents tag types. */
public enum TagType {
  EXCHANGE("exchange"),
  NON_EXCHANGE("non-exchange");

  private String type;

  TagType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public static TagType getTagType(String name) {
    TagType result = null;

    for (TagType tagType : values()) {
      if (tagType.getType().equals(name)) {
        result = tagType;
        break;
      }
    }

    return result;
  }
}
