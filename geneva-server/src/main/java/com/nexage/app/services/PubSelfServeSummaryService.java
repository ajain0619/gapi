package com.nexage.app.services;

import com.nexage.app.dto.pub.self.serve.PubSelfServeDashboardMetricsDTO;
import com.nexage.app.dto.pub.self.serve.PubSelfServeMediationRuleMetricsDTO;
import com.nexage.app.dto.pub.self.serve.PubSelfServeSiteMetrics;
import java.util.Date;

public interface PubSelfServeSummaryService {
  PubSelfServeDashboardMetricsDTO getDashboardSummary(long pubId, Date start, Date stop);

  PubSelfServeMediationRuleMetricsDTO getTagSummary(long pubId, Date start, Date stop);

  PubSelfServeSiteMetrics getTagSummary(long pubId, long siteId, Date start, Date stop);
}
