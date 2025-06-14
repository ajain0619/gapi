package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BidderFormatTest {
  @Test
  void shouldReturnExpectedOrder() {
    assertEquals(0, BidderFormat.OpenRTBv2.ordinal());
    assertEquals(1, BidderFormat.OpenRTBv2_2.ordinal());
    assertEquals(2, BidderFormat.OpenRTBv2_3.ordinal());
    assertEquals(3, BidderFormat.OpenRTBv2_3_1.ordinal());
    assertEquals(4, BidderFormat.OpenRTBv2_4.ordinal());
    assertEquals(5, BidderFormat.OpenRTBv2_4_1.ordinal());
    assertEquals(6, BidderFormat.OpenRTBv2_4_2.ordinal());
    assertEquals(7, BidderFormat.OpenRTBv2_5.ordinal());
    assertEquals(8, BidderFormat.OpenRTBv2_5_1.ordinal());
  }
}
