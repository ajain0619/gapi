package com.nexage.app.services;

import com.nexage.admin.core.bidder.model.BdrInsertionOrder;

public interface InsertionOrderService {

  BdrInsertionOrder getInsertionOrder(long seatholderPid, long pid);
}
