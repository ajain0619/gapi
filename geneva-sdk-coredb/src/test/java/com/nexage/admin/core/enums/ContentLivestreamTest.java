package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ContentLivestreamTest {
  @Test
  void shouldReturnExpectedOrder() {
    assertEquals(0, ContentLivestream.VOD.ordinal());
    assertEquals(1, ContentLivestream.LIVE.ordinal());
  }
}
