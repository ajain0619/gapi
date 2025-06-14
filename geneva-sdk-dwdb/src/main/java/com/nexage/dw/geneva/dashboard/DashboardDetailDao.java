package com.nexage.dw.geneva.dashboard;

import com.nexage.dw.geneva.dashboard.model.BuyerDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.NexageDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.SellerDashboardDetail;
import java.util.List;
import java.util.Set;

public interface DashboardDetailDao {

  /**
   * Gets BuyerMetrics for Geneva Dashboard
   *
   * @param start - Start time interval
   * @param stop - Stop time interval
   * @param companyPids - Company IDs
   * @return BuyerMetrics
   */
  List<BuyerDashboardDetail> getBuyerMetrics(String start, String stop, Set<Long> companyPids);

  /**
   * Gets BuyerMetrics for Geneva Dashboard for Nexage User
   *
   * @param start - Start time interval
   * @param stop - Stop time interval
   * @return BuyerMetrics
   */
  List<BuyerDashboardDetail> getBuyerMetrics(String start, String stop);

  /**
   * Gets SellerMetrics for Geneva Dashboard
   *
   * @param start - Start time interval
   * @param stop - Stop time interval
   * @param companyPids - Company IDs
   * @return SellerMetrics
   */
  List<SellerDashboardDetail> getSellerMetrics(String start, String stop, Set<Long> companyPids);

  /**
   * Gets SellerMetrics for Geneva Dashboard for Nexage User
   *
   * @param start - Start time interval
   * @param stop - Stop time interval
   * @return SellerMetrics
   */
  List<SellerDashboardDetail> getSellerMetrics(String start, String stop);

  /**
   * Gets NexageMetrics for Geneva Dashboard
   *
   * @param start - Start time interval
   * @param stop - Stop time interval
   * @return NexageMetrics
   */
  NexageDashboardDetail getNexageMetrics(String start, String stop);
}
