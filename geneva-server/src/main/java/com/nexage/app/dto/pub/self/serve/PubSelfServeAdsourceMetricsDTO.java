package com.nexage.app.dto.pub.self.serve;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.pubselfserve.AdsourcePubSelfServeView;
import com.nexage.admin.core.pubselfserve.AdsourcePubSelfServeView.AdSourceType;
import com.nexage.app.dto.BasePubSelfServeMetricsDTO;
import com.nexage.dw.geneva.util.ISO8601Util;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@Log4j2
public class PubSelfServeAdsourceMetricsDTO extends BasePubSelfServeMetricsDTO {

  private long pid;
  private String name;
  private AdSourceType type;
  private double revenueTrendPercent;
  @JsonIgnore private Date lastUpdate;
  private String updated;
  private String logoUrl;

  protected Logger logger = Logger.getLogger(this.getClass());

  public PubSelfServeAdsourceMetricsDTO(
      AdsourcePubSelfServeView adsource, AdSourceType type, String adSourceLogoBaseUrl) {
    this.pid = adsource.getPid();
    this.name = adsource.getName();
    this.type = type;
    this.logoUrl =
        (StringUtils.isNotBlank(adSourceLogoBaseUrl) && StringUtils.isNotBlank(adsource.getLogo()))
            ? adSourceLogoBaseUrl + adsource.getLogo()
            : null;
  }

  public long getPid() {
    return pid;
  }

  public void setPid(long pid) {
    this.pid = pid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AdSourceType getType() {
    return type;
  }

  public void setType(AdSourceType type) {
    this.type = type;
  }

  public double getRevenueTrendPercent() {
    return revenueTrendPercent;
  }

  public void setRevenueTrendPercent(double revenueTrendPercent) {
    this.revenueTrendPercent = revenueTrendPercent;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public String getUpdated() {
    try {
      if (lastUpdate != null) updated = ISO8601Util.format(lastUpdate);
    } catch (Exception e) {
      log.warn("Couldn not parse this date " + e.getMessage());
    }
    return updated;
  }

  public void aggregate(PubSelfServeMetrics m) {
    this.requests += m.getCurrentOutboundReqs();
    this.served += m.getCurrentServed();
    this.delivered += m.getCurrentDelivered();
    this.clicks += m.getCurrentClicks();
    this.revenue += m.getCurrentRevenue();
    this.preRevenue += m.getPrevRevenue();
    if (this.lastUpdate == null || this.lastUpdate.before(m.getUpdated())) {
      this.lastUpdate = m.getUpdated();
    }
  }
}
