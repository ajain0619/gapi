package com.nexage.dw.geneva.dashboard;

import com.nexage.dw.geneva.dashboard.model.DashboardMetric;
import java.util.Date;
import java.util.Set;

public interface DashboardFacade {

  /**
   * Gets the Sellers Metrics for Geneva Dashboard with Trends (if trend is true)
   *
   * @param start - Start time of the interval
   * @param stop - End time of the interval
   * @param companyPids - Company IDs
   * @param trend - boolean indicating if trend is requested (only true for certain time intervals)
   * @return DashboardMetric with Only Sellers Metrics populated
   */
  DashboardMetric getSellerMetrics(Date start, Date stop, Set<Long> companyPids, boolean trend);

  /**
   * Gets the Buyers Metrics for Geneva Dashboard with Trends (if trend is true)
   *
   * @param start - Start time of the interval
   * @param stop - End time of the interval
   * @param companyPids - Company IDs
   * @param trend - boolean indicating if trend is requested (only true for certain time intervals)
   * @return DashboardMetric with Only buyer Metrics populated
   */
  DashboardMetric getBuyerMetrics(Date start, Date stop, Set<Long> companyPids, boolean trend);

  /**
   * Gets the Nexage Metrics for Geneva Dashboard with Trends (if trend is true)
   *
   * @param start - Start time of the interval
   * @param stop - End time of the interval
   * @param trend - boolean indicating if trend is requested (only true for certain time intervals)
   * @return DashboardMetric with both SellerMetrics and BuyerMetrics populated
   */
  DashboardMetric getNexageMetrics(Date start, Date stop, boolean trend);
}
