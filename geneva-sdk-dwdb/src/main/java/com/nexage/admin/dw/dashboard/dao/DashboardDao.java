package com.nexage.admin.dw.dashboard.dao;

import com.nexage.admin.dw.dashboard.model.BuyerKeyMetrics;

public interface DashboardDao {

  /**
   * Get buyer metrics for given bidder and time range
   *
   * @param start - start time
   * @param stop - end time
   * @param bidderPid - bidder pid
   * @return SellerKeyMetrics
   */
  BuyerKeyMetrics getBuyerMetrics(String start, String stop, Long bidderPid);
}
