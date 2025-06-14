package com.nexage.admin.core.util;

import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.type.BDRLineItemStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BDRInsertionOrderHelper {

  public static final int getInsertionOrderStatusFromLineItemStatuses(
      final List<Integer> lineItemStatusList) {

    BDRLineItemStatus state = BDRLineItemStatus.INACTIVE;

    if (CollectionUtils.isEmpty(lineItemStatusList)) return state.asInt();

    if (lineItemStatusList.contains(BDRLineItemStatus.ACTIVE.asInt())) // Running
    state = BDRLineItemStatus.ACTIVE;
    else if (!lineItemStatusList.contains(BDRLineItemStatus.ACTIVE.asInt())
        && lineItemStatusList.contains(BDRLineItemStatus.PAUSED.asInt())) // Paused
    state = BDRLineItemStatus.PAUSED;
    else if (!lineItemStatusList.contains(BDRLineItemStatus.ACTIVE.asInt())
        && !lineItemStatusList.contains(BDRLineItemStatus.PAUSED.asInt())
        && lineItemStatusList.contains(BDRLineItemStatus.SCHEDULED.asInt())) // Scheduled
    state = BDRLineItemStatus.SCHEDULED;
    else if (!lineItemStatusList.contains(BDRLineItemStatus.ACTIVE.asInt())
        && !lineItemStatusList.contains(BDRLineItemStatus.PAUSED.asInt())
        && !lineItemStatusList.contains(BDRLineItemStatus.SCHEDULED.asInt())
        && lineItemStatusList.contains(BDRLineItemStatus.INACTIVE.asInt())) // Inactive
    state = BDRLineItemStatus.INACTIVE;
    else if (!lineItemStatusList.contains(BDRLineItemStatus.ACTIVE.asInt())
        && !lineItemStatusList.contains(BDRLineItemStatus.PAUSED.asInt())
        && !lineItemStatusList.contains(BDRLineItemStatus.SCHEDULED.asInt())
        && !lineItemStatusList.contains(BDRLineItemStatus.INACTIVE.asInt())
        && lineItemStatusList.contains(BDRLineItemStatus.COMPLETED.asInt())) // Completed
    state = BDRLineItemStatus.COMPLETED;
    else if (!lineItemStatusList.contains(BDRLineItemStatus.ACTIVE.asInt())
        && !lineItemStatusList.contains(BDRLineItemStatus.PAUSED.asInt())
        && !lineItemStatusList.contains(BDRLineItemStatus.SCHEDULED.asInt())
        && !lineItemStatusList.contains(BDRLineItemStatus.INACTIVE.asInt())
        && !lineItemStatusList.contains(BDRLineItemStatus.COMPLETED.asInt())
        && lineItemStatusList.contains(BDRLineItemStatus.ARCHIVED.asInt())) // Archive
    state = BDRLineItemStatus.ARCHIVED;

    return state.asInt();
  }

  public static final BDRLineItemStatus getInsertionOrderStatusFromLineItems(
      List<BDRLineItem> lineItems) {

    final List<Integer> lineItemStatusList = new ArrayList<>(lineItems.size());

    for (BDRLineItem lineItem : lineItems) {
      lineItemStatusList.add(lineItem.getStatus().asInt());
    }

    int status = getInsertionOrderStatusFromLineItemStatuses(lineItemStatusList);
    return BDRLineItemStatus.fromInt(status);
  }

  public static final BDRLineItemStatus getInsertionOrderStatusFromLineItemsWithDate(
      List<BDRLineItem> lineItems, long offset) {

    Date now = new Date();

    final List<Integer> lineItemStatusList = new ArrayList<>(lineItems.size());

    for (BDRLineItem lineItem : lineItems) {
      Date waitingPoint = new Date(lineItem.getUpdatedOn().getTime() + offset);
      // This is a bit of a hack.  We return Active so the line item and I/O stay in SMC for the
      // offest duration after status change
      lineItemStatusList.add(
          (lineItem.getStatus() == BDRLineItemStatus.COMPLETED
                      || lineItem.getStatus() == BDRLineItemStatus.ARCHIVED)
                  && waitingPoint.after(now)
              ? BDRLineItemStatus.ACTIVE.asInt()
              : lineItem.getStatus().asInt());
    }

    int status = getInsertionOrderStatusFromLineItemStatuses(lineItemStatusList);
    return BDRLineItemStatus.fromInt(status);
  }
}
