package com.nexage.app.dto.pub.self.serve;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.pubselfserve.AdsourcePubSelfServeView.AdSourceType;
import com.nexage.admin.core.pubselfserve.PositionPubSelfServeView;
import com.nexage.admin.core.pubselfserve.SitePubSelfServeView;
import com.nexage.admin.core.pubselfserve.TagPubSelfServeView;
import com.nexage.app.services.impl.support.PubSelfServeSummaryContext;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PubSelfServeDashboardMetricsDTO {

  private long pid;
  private PubSelfServeSummaryMetrics summary;

  @JsonIgnore private Map<Long, PubSelfServeSiteMetrics> siteMetrics = new HashMap<>();

  @JsonIgnore private Map<Long, PubSelfServeAdsourceMetricsDTO> adsourceMetrics = new HashMap<>();

  public PubSelfServeDashboardMetricsDTO(
      long pid,
      List<SitePubSelfServeView> sites,
      PubSelfServeSummaryContext context,
      List<Long> nexageRtbList) {
    this.pid = pid;
    this.summary = new PubSelfServeSummaryMetrics();
    for (SitePubSelfServeView site : sites) {
      PubSelfServeSiteMetrics siteMetric = PubSelfServeSiteMetrics.buildSite(site);
      for (PositionPubSelfServeView pos : site.getPositions().values()) {
        PubSelfServePositionMetrics posMetric = new PubSelfServePositionMetrics(pos);
        for (TagPubSelfServeView pubTag : pos.getTags()) {
          // MX-3070 decision maker tags should be filtered out from the response
          Integer tierType = pubTag.getTierType();
          if (!(tierType != null && tierType.equals(TierType.SY_DECISION_MAKER.asInt()))) {
            PubSelfServeTagMetricsDTO tagMetric =
                new PubSelfServeTagMetricsDTO(
                    pubTag,
                    context.getSiteFromPid(site.getPid()),
                    context.getTagFromPid(pubTag.getPid()),
                    context.getBuyerLogoBaseUrl());
            posMetric.getTagMetrics().put(pubTag.getPid(), tagMetric);
            PubSelfServeAdsourceMetricsDTO adsourceMetric =
                new PubSelfServeAdsourceMetricsDTO(
                    pubTag.getAdsource(),
                    nexageRtbList.contains(pubTag.getAdsource().getPid())
                        ? AdSourceType.RTB
                        : AdSourceType.Mediation,
                    context.getBuyerLogoBaseUrl());
            adsourceMetrics.put(adsourceMetric.getPid(), adsourceMetric);
          }
        }
        siteMetric.getPosMetrics().put(pos.getName(), posMetric);
      }
      siteMetrics.put(site.getPid(), siteMetric);
    }
  }

  public void aggregate(Map<Long, List<PubSelfServeMetrics>> metricsMap) {
    for (PubSelfServeSiteMetrics site : siteMetrics.values()) {
      List<PubSelfServeMetrics> metrics = metricsMap.get(site.getPid());
      if (metrics != null) {
        for (PubSelfServeMetrics metric : metrics) {
          summary.aggregate(metric);
          site.aggregate(metric);
          if (metric.getAdnetId() != -1) {
            PubSelfServeAdsourceMetricsDTO adsource = adsourceMetrics.get(metric.getAdnetId());
            if (adsource != null) {
              adsource.aggregate(metric);
            } else {

            }
          }
        }
      }
    }
  }

  public long getPid() {
    return pid;
  }

  public PubSelfServeSummaryMetrics getSummary() {
    return summary;
  }

  public Collection<PubSelfServeAdsourceMetricsDTO> getAdsources() {
    return adsourceMetrics.values();
  }

  public Collection<PubSelfServeSiteMetrics> getSites() {
    return siteMetrics.values();
  }
}
