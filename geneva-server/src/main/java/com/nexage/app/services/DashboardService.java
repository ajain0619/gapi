package com.nexage.app.services;

import com.nexage.admin.core.pubselfserve.SitePubSelfServeView;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DashboardService {

  DashboardMetric getBuyerDashboard(Date start, Date stop, boolean trend);

  DashboardMetric getSellerDashboard(Date start, Date stop, boolean trend);

  DashboardMetric getNexageDashboard(Date start, Date stop, boolean trend);

  Map<Long, List<PubSelfServeMetrics>> getSummaryMetricsDefault(long pubId, Date start, Date stop);

  Map<Long, List<PubSelfServeMetrics>> getSummaryMetricsOptimized(
      long pubId, List<SitePubSelfServeView> sites, Date start, Date stop);
}
