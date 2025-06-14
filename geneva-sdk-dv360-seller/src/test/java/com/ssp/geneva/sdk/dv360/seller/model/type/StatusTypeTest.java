package com.ssp.geneva.sdk.dv360.seller.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StatusTypeTest {

  @Test
  void shouldReturnStatusTypeString() {
    assertEquals(
        "DISCOVERY_OBJECT_STATUS_UNSPECIFIED,DISCOVERY_OBJECT_STATUS_REMOVED,DISCOVERY_OBJECT_STATUS_PAUSED,DISCOVERY_OBJECT_STATUS_ACTIVE,DISCOVERY_OBJECT_STATUS_ARCHIVED",
        StatusType.getValues());
  }

  @Test
  void shouldReturnOptionalStatusType() {
    assertEquals(
        StatusType.DISCOVERY_OBJECT_STATUS_UNSPECIFIED,
        StatusType.getStatusType("DISCOVERY_OBJECT_STATUS_UNSPECIFIED").get());
  }

  @Test
  void shouldReturnEmptyOptional() {
    assertTrue(StatusType.getStatusType("DISCOVERY_OBJECT_STATUS").isEmpty());
  }
}
