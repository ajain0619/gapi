package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RtbProfileGroupTest {

  private RtbProfileGroup rtbProfileGroup;

  @BeforeEach
  void setup() {
    rtbProfileGroup = new RtbProfileGroup();
    rtbProfileGroup.setItemType(RtbProfileGroup.ItemType.CATEGORY);
    rtbProfileGroup.setListType(RtbProfileGroup.ListType.BLOCKLIST);
  }

  @Test
  void shouldReturnItemTypeByInt() {
    assertEquals(RtbProfileGroup.ItemType.CATEGORY, RtbProfileGroup.ItemType.fromInt(0));
    assertEquals(RtbProfileGroup.ItemType.ADOMAIN, RtbProfileGroup.ItemType.fromInt(1));
    assertEquals(RtbProfileGroup.ItemType.BIDDER, RtbProfileGroup.ItemType.fromInt(2));
  }

  @Test
  void shouldReturnListTypeByInt() {
    assertEquals(RtbProfileGroup.ListType.BLOCKLIST, RtbProfileGroup.ListType.fromInt(0));
    assertEquals(RtbProfileGroup.ListType.WHITELIST, RtbProfileGroup.ListType.fromInt(1));
  }

  @Test
  void shouldThrowOnIllegalValue() {
    assertThrows(IllegalArgumentException.class, () -> RtbProfileGroup.ItemType.fromInt(3));
    assertThrows(IllegalArgumentException.class, () -> RtbProfileGroup.ListType.fromInt(2));
  }

  @Test
  void shouldReturnExternalValue() {
    RtbProfileGroup.ListType listType = rtbProfileGroup.getListType();
    RtbProfileGroup.ItemType itemType = rtbProfileGroup.getItemType();

    assertEquals(0, listType.getValue());
    assertEquals(0, itemType.getValue());
  }
}
