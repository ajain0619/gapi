package com.nexage.app.dto.pub.self.serve;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.pubselfserve.PositionPubSelfServeView;
import com.nexage.app.dto.BasePubSelfServeMetricsDTO;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PubSelfServePositionMetrics extends BasePubSelfServeMetricsDTO {

  private final long pid;
  private final String name;
  private final String memo;
  private final String type;

  @JsonIgnore private Map<Long, PubSelfServeTagMetricsDTO> tagMetrics = new HashMap<>();

  private PlacementCategory placementCategory;
  private final boolean interstitial;
  private final TrafficType trafficType;

  public PubSelfServePositionMetrics(PositionPubSelfServeView position) {
    this.pid = position.getPid();
    this.name = position.getName();
    this.memo = position.getMemo();
    this.type = position.getType();
    this.placementCategory = position.getPlacementCategory();
    Boolean interstitialValue = position.getInterstitial();
    this.interstitial = (interstitialValue != null) && interstitialValue;
    this.trafficType = position.getTrafficType();
  }

  public long getPid() {
    return pid;
  }

  public PlacementCategory getPlacementCategory() {
    return placementCategory;
  }

  public String getName() {
    return name;
  }

  public String getMemo() {
    return memo;
  }

  public String getType() {
    return type;
  }

  public boolean isInterstitial() {
    return interstitial;
  }

  public Collection<PubSelfServeTagMetricsDTO> getTags() {
    return tagMetrics.values();
  }

  public Map<Long, PubSelfServeTagMetricsDTO> getTagMetrics() {
    return tagMetrics;
  }

  public TrafficType getTrafficType() {
    return trafficType;
  }

  public void aggregate(PubSelfServeMetrics m) {
    this.requests += m.getCurrentInboundReqs();
    this.served += m.getCurrentServed();
    this.delivered += m.getCurrentDelivered();
    this.clicks += m.getCurrentClicks();
    this.revenue += m.getCurrentRevenue();
    this.preRevenue += m.getPrevRevenue();

    if (m.getTagId() != -1) {
      PubSelfServeTagMetricsDTO tag = tagMetrics.get(m.getTagId());
      if (tag != null) {
        tag.aggregate(m);
      } else {

      }
    }
  }
}
