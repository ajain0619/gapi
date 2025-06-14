package com.nexage.app.dto.pub.self.serve;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.pubselfserve.SitePubSelfServeView;
import com.nexage.app.services.impl.support.PubSelfServeSummaryContext;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PubSelfServeMediationRuleMetricsDTO {

  private long pid;

  @JsonIgnore private Map<Long, PubSelfServeSiteMetrics> siteMetrics = new HashMap<>();

  public PubSelfServeMediationRuleMetricsDTO(
      long pid,
      List<SitePubSelfServeView> pubSites,
      PubSelfServeSummaryContext context,
      List<Long> nexageRtbList) {
    this.pid = pid;
    for (SitePubSelfServeView pubSite : pubSites) {
      PubSelfServeSiteMetrics siteMetric =
          PubSelfServeSiteMetrics.buildSiteWithChildren(pubSite, context);
      siteMetrics.put(pubSite.getPid(), siteMetric);
    }
  }

  public void aggregate(Map<Long, List<PubSelfServeMetrics>> metricsMap) {
    for (PubSelfServeSiteMetrics site : siteMetrics.values()) {
      List<PubSelfServeMetrics> metrics = metricsMap.get(site.getPid());
      if (metrics != null) {
        for (PubSelfServeMetrics metric : metrics) {
          site.aggregate(metric);
        }
      }
    }
  }

  public long getPid() {
    return pid;
  }

  public Collection<PubSelfServeSiteMetrics> getSites() {
    return siteMetrics.values();
  }
}
