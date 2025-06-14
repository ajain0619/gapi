package com.ssp.geneva.sdk.dv360.seller.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ModelTypeTest {
  @Test
  void test() {
    assertEquals("CREATIVE_TYPE_DISPLAY", CreativeType.CREATIVE_TYPE_DISPLAY.name());
    assertEquals(
        "DURATION_MATCH_TYPE_EQUAL_TO", DurationMatchType.DURATION_MATCH_TYPE_EQUAL_TO.name());
    assertEquals("DEAL_FORMAT_DISPLAY", FormatType.DEAL_FORMAT_DISPLAY.name());
    assertEquals("MEDIUM_TYPE_DIGITAL", MediumType.MEDIUM_TYPE_DIGITAL.name());
    assertEquals("ACCEPTED", OrderStatusType.ACCEPTED.name());
    assertEquals("CPM", RateType.CPM.name());
    assertEquals("SKIPPABLE_MATCH_TYPE_ANY", SkippableMatchType.SKIPPABLE_MATCH_TYPE_ANY.name());
    assertEquals(
        "DISCOVERY_OBJECT_STATUS_PAUSED", StatusType.DISCOVERY_OBJECT_STATUS_PAUSED.name());
    assertEquals("RESERVED", TransactionType.RESERVED.name());
  }
}
