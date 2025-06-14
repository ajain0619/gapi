package com.nexage.app.dto.publisher;

import java.io.Serializable;
import java.util.HashMap;

public class PublisherRTBProfileGroupDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long pid;
  private String name;
  private Integer version;
  private Long publisherPid;
  private String privilegeLevel;
  private String data;
  private ItemType itemType;
  private ListType listType;
  private boolean isIndividualsGroup;

  public PublisherRTBProfileGroupDTO() {}

  public PublisherRTBProfileGroupDTO(
      Long pid,
      String name,
      Integer version,
      String privilegeLevel,
      String data,
      int itemType,
      int listType,
      Long publisherPid,
      boolean isIndividualsGroup) {
    this.pid = pid;
    this.name = name;
    this.version = version;
    this.privilegeLevel = privilegeLevel;
    this.data = data;
    this.itemType = ItemType.fromInt(itemType);
    this.listType = ListType.fromInt(listType);
    this.publisherPid = publisherPid;
    this.isIndividualsGroup = isIndividualsGroup;
  }

  public Long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public Integer getVersion() {
    return version;
  }

  public Long getPublisherPid() {
    return publisherPid;
  }

  public void setPublisherPid(Long pid) {
    this.publisherPid = pid;
  }

  public String getPrivilegeLevel() {
    return privilegeLevel;
  }

  public String getData() {
    return data;
  }

  public ItemType getItemType() {
    return itemType;
  }

  public ListType getListType() {
    return listType;
  }

  public boolean getIsIndividualsGroup() {
    return isIndividualsGroup;
  }

  public void setIsIndividualsGroup(boolean isIndividualsGroup) {
    this.isIndividualsGroup = isIndividualsGroup;
  }

  public enum ItemType {
    CATEGORY(0),
    ADOMAIN(1),
    BIDDER(2);

    private final int externalValue;

    private ItemType(int externalValue) {
      this.externalValue = externalValue;
    }

    public int getExternalValue() {
      return externalValue;
    }

    private static final HashMap<Integer, ItemType> fromIntMap = new HashMap<>();

    static {
      for (ItemType type : ItemType.values()) {
        fromIntMap.put(type.externalValue, type);
      }
    }

    public static ItemType fromInt(int value) {
      return fromIntMap.get(value);
    }

    public int asInt() {
      return externalValue;
    }
  }

  public enum ListType {
    BLOCKLIST(0),
    WHITELIST(1);

    private final int externalValue;

    private ListType(int externalValue) {
      this.externalValue = externalValue;
    }

    public int getExternalValue() {
      return externalValue;
    }

    private static final HashMap<Integer, ListType> fromIntMap = new HashMap<>();

    static {
      for (ListType type : ListType.values()) {
        fromIntMap.put(type.externalValue, type);
      }
    }

    public static ListType fromInt(int value) {
      return fromIntMap.get(value);
    }

    public int asInt() {
      return externalValue;
    }
  }
}
