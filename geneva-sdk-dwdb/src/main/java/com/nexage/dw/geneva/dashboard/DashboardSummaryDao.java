package com.nexage.dw.geneva.dashboard;

import com.nexage.dw.geneva.dashboard.model.BuyerDashboardSummary;
import com.nexage.dw.geneva.dashboard.model.NexageDashboardSummary;
import com.nexage.dw.geneva.dashboard.model.SellerDashboardSummary;
import java.util.Set;

public interface DashboardSummaryDao {

  /**
   * Gets BuyerMetrics for Geneva Dashboard
   *
   * @param start - Start time interval
   * @param stop - Stop time interval
   * @param companyPids - Company IDs
   * @return BuyerMetrics
   */
  BuyerDashboardSummary getBuyerMetrics(String start, String stop, Set<Long> companyPids);

  /**
   * Gets BuyerMetrics for Geneva Dashboard for Nexage logged in Users
   *
   * @param start - Start time interval
   * @param stop - Stop time interval
   * @return BuyerMetrics
   */
  BuyerDashboardSummary getBuyerMetrics(String start, String stop);

  /**
   * Gets SellerMetrics for Geneva Dashboard
   *
   * @param start - Start time interval
   * @param stop - Stop time interval
   * @param companyPids - Company IDs
   * @return SellerMetrics
   */
  SellerDashboardSummary getSellerMetrics(String start, String stop, Set<Long> companyPids);

  /**
   * Gets SellerMetrics for Geneva Dashboard for Nexage logged in Users
   *
   * @param start - Start time interval
   * @param stop - Stop time interval
   * @return SellerMetrics
   */
  SellerDashboardSummary getSellerMetrics(String start, String stop);

  /**
   * Gets NexageMetrics for Geneva Dashboard
   *
   * @param start - Start time interval
   * @param stop - Stop time interval
   * @return NexageMetrics
   */
  NexageDashboardSummary getNexageMetrics(String start, String stop);
}
