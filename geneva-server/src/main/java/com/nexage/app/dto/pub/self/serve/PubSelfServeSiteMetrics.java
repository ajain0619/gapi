package com.nexage.app.dto.pub.self.serve;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.enums.Mode;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.pubselfserve.PositionPubSelfServeView;
import com.nexage.admin.core.pubselfserve.SitePubSelfServeView;
import com.nexage.admin.core.pubselfserve.TagPubSelfServeView;
import com.nexage.app.dto.BasePubSelfServeMetricsDTO;
import com.nexage.app.services.impl.support.PubSelfServeSummaryContext;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PubSelfServeSiteMetrics extends BasePubSelfServeMetricsDTO {

  private final long pid;
  private final String name;
  private final Type type;
  private final Platform platform;

  @JsonIgnore private Map<String, PubSelfServePositionMetrics> posMetrics = new HashMap<>();

  private final Status status;
  private final Mode mode;
  private final String dcn;

  private PubSelfServeSiteMetrics(SitePubSelfServeView site) {
    this.pid = site.getPid();
    this.name = site.getName();
    this.type = site.getType();
    this.platform = site.getPlatform();
    this.status = site.getStatus();
    this.mode = site.getMode();
    this.dcn = site.getDcn();
  }

  public static PubSelfServeSiteMetrics buildSiteWithChildren(
      SitePubSelfServeView pubSite, PubSelfServeSummaryContext context) {
    PubSelfServeSiteMetrics metric = new PubSelfServeSiteMetrics(pubSite);
    for (PositionPubSelfServeView pos : pubSite.getPositions().values()) {
      PubSelfServePositionMetrics posMetric = new PubSelfServePositionMetrics(pos);
      for (TagPubSelfServeView tag : pos.getTags()) {
        PubSelfServeTagMetricsDTO tagMetric =
            new PubSelfServeTagMetricsDTO(
                tag,
                context.getSiteFromPid(pubSite.getPid()),
                context.getTagFromPid(tag.getPid()),
                context.getBuyerLogoBaseUrl());
        posMetric.getTagMetrics().put(tag.getPid(), tagMetric);
      }
      metric.posMetrics.put(pos.getName(), posMetric);
    }
    return metric;
  }

  public static PubSelfServeSiteMetrics buildSite(SitePubSelfServeView site) {
    return new PubSelfServeSiteMetrics(site);
  }

  public long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  public Platform getPlatform() {
    return platform;
  }

  public Map<String, PubSelfServePositionMetrics> getPosMetrics() {
    return posMetrics;
  }

  public Collection<PubSelfServePositionMetrics> getPositions() {
    return posMetrics.values();
  }

  public String getDcn() {
    return dcn;
  }

  public Status getStatus() {
    return status;
  }

  public Mode getMode() {
    return mode;
  }

  public void aggregate(PubSelfServeMetrics m) {
    this.requests += m.getCurrentInboundReqs();
    this.served += m.getCurrentServed();
    this.delivered += m.getCurrentDelivered();
    this.clicks += m.getCurrentClicks();
    this.revenue += m.getCurrentRevenue();
    this.preRevenue += m.getPrevRevenue();

    PubSelfServePositionMetrics pos = posMetrics.get(m.getPosition());
    if (pos != null) {
      pos.aggregate(m);
    }
  }
}
