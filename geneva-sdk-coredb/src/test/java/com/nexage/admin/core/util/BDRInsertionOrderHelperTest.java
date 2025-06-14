package com.nexage.admin.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.bidder.type.BDRLineItemStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class BDRInsertionOrderHelperTest {

  @Test
  void testGetInsertionOrderStatusFromLineItemStatuses() {
    int state =
        BDRInsertionOrderHelper.getInsertionOrderStatusFromLineItemStatuses(
            getLineItemRunningStatus());
    assertEquals(BDRLineItemStatus.ACTIVE.asInt(), state);

    state =
        BDRInsertionOrderHelper.getInsertionOrderStatusFromLineItemStatuses(
            getLineItemPausedStatus());
    assertEquals(BDRLineItemStatus.PAUSED.asInt(), state);

    state =
        BDRInsertionOrderHelper.getInsertionOrderStatusFromLineItemStatuses(
            getLineItemScheduledStatus());
    assertEquals(BDRLineItemStatus.SCHEDULED.asInt(), state);

    state =
        BDRInsertionOrderHelper.getInsertionOrderStatusFromLineItemStatuses(
            getLineItemInactiveStatus());
    assertEquals(BDRLineItemStatus.INACTIVE.asInt(), state);

    state =
        BDRInsertionOrderHelper.getInsertionOrderStatusFromLineItemStatuses(
            getLineItemCompleteStatus());
    assertEquals(BDRLineItemStatus.COMPLETED.asInt(), state);

    state =
        BDRInsertionOrderHelper.getInsertionOrderStatusFromLineItemStatuses(
            getLineItemArchivedStatus());
    assertEquals(BDRLineItemStatus.ARCHIVED.asInt(), state);
  }

  private List<Integer> getLineItemRunningStatus() {
    List<Integer> statuses = new ArrayList<>();
    statuses.add(BDRLineItemStatus.ACTIVE.asInt());
    statuses.add(BDRLineItemStatus.PAUSED.asInt());
    statuses.add(BDRLineItemStatus.SCHEDULED.asInt());
    statuses.add(BDRLineItemStatus.INACTIVE.asInt());
    statuses.add(BDRLineItemStatus.COMPLETED.asInt());
    statuses.add(BDRLineItemStatus.ARCHIVED.asInt());
    return statuses;
  }

  private List<Integer> getLineItemPausedStatus() {
    List<Integer> statuses = new ArrayList<>();
    statuses.add(BDRLineItemStatus.PAUSED.asInt());
    statuses.add(BDRLineItemStatus.SCHEDULED.asInt());
    statuses.add(BDRLineItemStatus.INACTIVE.asInt());
    statuses.add(BDRLineItemStatus.COMPLETED.asInt());
    statuses.add(BDRLineItemStatus.ARCHIVED.asInt());
    return statuses;
  }

  private List<Integer> getLineItemScheduledStatus() {
    List<Integer> statuses = new ArrayList<>();
    statuses.add(BDRLineItemStatus.SCHEDULED.asInt());
    statuses.add(BDRLineItemStatus.INACTIVE.asInt());
    statuses.add(BDRLineItemStatus.COMPLETED.asInt());
    statuses.add(BDRLineItemStatus.ARCHIVED.asInt());
    return statuses;
  }

  private List<Integer> getLineItemInactiveStatus() {
    List<Integer> statuses = new ArrayList<>();
    statuses.add(BDRLineItemStatus.INACTIVE.asInt());
    statuses.add(BDRLineItemStatus.COMPLETED.asInt());
    statuses.add(BDRLineItemStatus.ARCHIVED.asInt());
    return statuses;
  }

  private List<Integer> getLineItemCompleteStatus() {
    List<Integer> statuses = new ArrayList<>();
    statuses.add(BDRLineItemStatus.COMPLETED.asInt());
    statuses.add(BDRLineItemStatus.ARCHIVED.asInt());
    return statuses;
  }

  private List<Integer> getLineItemArchivedStatus() {
    List<Integer> statuses = new ArrayList<>();
    statuses.add(BDRLineItemStatus.ARCHIVED.asInt());
    return statuses;
  }
}
