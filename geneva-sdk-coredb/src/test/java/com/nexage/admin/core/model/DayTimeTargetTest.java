package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.bidder.model.BDRTarget;
import com.nexage.admin.core.bidder.type.BDRTargetType;
import org.junit.jupiter.api.Test;

class DayTimeTargetTest {

  @Test
  void dayTimeTargetReverseFormattingTest() {
    String data = "Sun/09:00/570,Sun/23:15/45";
    BDRTarget bdrTarget = new BDRTarget();
    bdrTarget.setTargetType(BDRTargetType.DAYTIME);
    bdrTarget.setData(data);
    assertEquals("Sun/09:00/18:30,Sun/23:15/00:00", bdrTarget.getData());
    bdrTarget.setData("Sun/23:15/0");
    assertEquals("Sun/23:15/23:15", bdrTarget.getData());
    bdrTarget.setData("Mon/00:15/30");
    assertEquals("Mon/00:15/00:45", bdrTarget.getData());
  }

  @Test
  void nonDayTimeTarget() {
    BDRTarget bdrTarget = new BDRTarget();
    bdrTarget.setTargetType(BDRTargetType.DEVICE);
    assertNull(bdrTarget.getData());
  }
}
