package com.ssp.geneva.server.report.report.impl.seller.impressiongroups;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImpressionGroupsResponse implements ReportResponse {
  private Long siteAdServes;
  private Long adRequests;
  private Long adServed;
  private Long adDelivered;
  private Long adClicks;
  private Double fillRate;
  private Double ctr;
  private Double revenue;
  private Double cpm;
  private Double rpm;
  private Double mmRevenue;
  private Double totalRevenue;
  private Double totalEcpm;
  private Double totalrpm;
  private String interval;
  private boolean outboundRequest;
  private String siteId;
  private String site;
  private String group;

  public ImpressionGroupsResponse() {}

  public ImpressionGroupsResponse(String interval) {
    this.siteAdServes = 0L;
    this.adRequests = 0L;
    this.adServed = 0L;
    this.adDelivered = 0L;
    this.adClicks = 0L;
    this.fillRate = 0d;
    this.ctr = 0d;
    this.revenue = 0d;
    this.cpm = 0d;
    this.rpm = 0d;
    this.mmRevenue = 0d;
    this.totalRevenue = 0d;
    this.totalEcpm = 0d;
    this.totalrpm = 0d;
    this.interval = interval;
  }

  @Override
  public ResponseMetadata getResponseMetadata() {
    return null;
  }

  public void postInitialize() {
    this.cpm = 0d;
    this.rpm = 0d;
    this.fillRate = 0d;
    this.ctr = 0d;
    if (this.adDelivered > 0) {
      this.cpm = (this.revenue / (double) this.adDelivered) * 1000;
    }
    if (this.adRequests > 0) {
      this.rpm = (this.revenue / (double) this.adRequests) * 1000;
    }
    if (this.adRequests > 0) {
      this.fillRate = (this.adServed / (double) this.adRequests) * 100;
      if (this.fillRate > 100) {
        this.fillRate = 100d;
      }
    }
    if (this.adDelivered > 0) {
      this.ctr = (this.adClicks / (double) this.adDelivered) * 100;
      if (this.ctr > 100) {
        this.ctr = 100d;
      }
    }
    if (totalRevenue != null) {
      this.totalEcpm = 0d;
      this.totalrpm = 0d;
      if (this.adDelivered > 0) {
        this.totalEcpm = (this.totalRevenue / (double) this.adDelivered) * 1000;
      }
      if (this.adRequests > 0) {
        this.totalrpm = (this.totalRevenue / (double) this.adRequests) * 1000;
      }
    }
    this.siteAdServes = null; // this is used internally and should not be returned in the response.
  }

  public Long getAdRequests() {
    return adRequests;
  }

  public void setAdRequests(Long adRequests) {
    this.adRequests = adRequests;
  }

  public Long getAdServed() {
    return adServed;
  }

  public void setAdServed(Long adServed) {
    this.adServed = adServed;
  }

  public Long getAdDelivered() {
    return adDelivered;
  }

  public void setAdDelivered(Long adDelivered) {
    this.adDelivered = adDelivered;
  }

  public Long getAdClicks() {
    return adClicks;
  }

  public void setAdClicks(Long adClicks) {
    this.adClicks = adClicks;
  }

  public Double getFillRate() {
    return fillRate;
  }

  public void setFillRate(Double fillRate) {
    this.fillRate = fillRate;
  }

  public Double getCtr() {
    return ctr;
  }

  public void setCtr(Double ctr) {
    this.ctr = ctr;
  }

  public Double getRevenue() {
    return revenue;
  }

  public void setRevenue(Double revenue) {
    this.revenue = revenue;
  }

  public Double getCpm() {
    return cpm;
  }

  public void setCpm(Double cpm) {
    this.cpm = cpm;
  }

  public Double getRpm() {
    return rpm;
  }

  public void setRpm(Double rpm) {
    this.rpm = rpm;
  }

  public Double getMmRevenue() {
    return mmRevenue;
  }

  public void setMmRevenue(Double mmRevenue) {
    this.mmRevenue = mmRevenue;
  }

  public Double getTotalRevenue() {
    return totalRevenue;
  }

  public void setTotalRevenue(Double totalRevenue) {
    this.totalRevenue = totalRevenue;
  }

  public Double getTotalEcpm() {
    return totalEcpm;
  }

  public void setTotalEcpm(Double totalEcpm) {
    this.totalEcpm = totalEcpm;
  }

  public Double getTotalrpm() {
    return totalrpm;
  }

  public void setTotalrpm(Double totalRpm) {
    this.totalrpm = totalRpm;
  }

  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public boolean isOutboundRequest() {
    return outboundRequest;
  }

  public void setOutboundRequest(boolean outboundRequest) {
    this.outboundRequest = outboundRequest;
  }

  public String getSiteId() {
    return siteId;
  }

  public void setSiteId(String siteId) {
    this.siteId = siteId;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public Long getSiteAdServes() {
    return siteAdServes;
  }

  public void setSiteAdServes(Long siteAdServes) {
    this.siteAdServes = siteAdServes;
  }
}
