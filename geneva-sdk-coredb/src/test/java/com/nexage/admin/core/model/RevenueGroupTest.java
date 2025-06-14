package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class RevenueGroupTest {

  @Test
  void shouldPopulateDateFields() {
    // given
    RevenueGroup revenueGroup = new RevenueGroup();

    // when
    revenueGroup.setTimestamps();

    // then
    assertNotNull(revenueGroup.getCreatedOn());
    assertNotNull(revenueGroup.getUpdatedOn());
    assertEquals(revenueGroup.getCreatedOn(), revenueGroup.getUpdatedOn());
  }
}
