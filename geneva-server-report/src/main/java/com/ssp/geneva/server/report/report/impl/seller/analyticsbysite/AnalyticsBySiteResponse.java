package com.ssp.geneva.server.report.report.impl.seller.analyticsbysite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;

/** Response DTO for one report entry of ANALYTICS_BY_SITE report. */
@JsonInclude(Include.NON_NULL)
public class AnalyticsBySiteResponse implements ReportResponse {

  @Override
  public ResponseMetadata getResponseMetadata() {
    return null; // TODO implement
  }

  private Long adRequests;
  private Long adServed;
  private Long adDelivered;
  private Long adClicks;
  private Double fillRate;
  private Double ctr;
  private Boolean outboundRequest = false;

  private Long bots;
  private Long frauds;

  // dim = site
  private Long siteId;
  private String site;

  // dim = position
  private String position;

  // dim = adsource
  private Long adSourceId;
  private String adSource;

  // dim = adtag
  private Long adTagId;
  private String adTag;

  // dim = country
  private String country;

  // dim = make
  private String deviceMake;

  // dim = model
  private String deviceModel;

  // dim = os
  private String deviceOS;

  // dim = osver
  private String deviceOSVersion;

  // dim = carrier
  private String carrier;

  // dim=day, week, month
  private String interval;

  // zero fields for cucumber
  // TODO remove this fields from this class and from all cucumber expected jsons for analytics
  // tests
  private Double cpm = 0.0;
  private Double revenue = 0.0;
  private Double rpm = 0.0;
  private Double totalRevenue = 0.0;
  private Double totalrpm = 0.0;
  private Double totalEcpm = 0.0;
  private Double mmRevenue = 0.0;

  public AnalyticsBySiteResponse() {}

  public AnalyticsBySiteResponse(String date) {
    this.interval = date;
    this.adRequests = 0L;
    this.adServed = 0L;
    this.adDelivered = 0L;
    this.adClicks = 0L;
    this.fillRate = 0.0;
    this.ctr = 0.0;
    this.bots = 0L;
    this.frauds = 0L;
  }

  private Double getCalculatedFillRate() {
    Double fillRate = 0.0;
    if (this.adRequests != null && this.adRequests > 0 && this.adServed != null) {
      fillRate = (this.adServed / (double) this.adRequests) * 100.0;
      if (fillRate > 100.0) {
        fillRate = 100.0;
      }
    }
    return fillRate;
  }

  private Double getCalculatedCtr() {
    Double ctr = 0.0;
    if (this.adDelivered != null && this.adDelivered > 0 && this.adClicks != null) {
      ctr = (this.adClicks / (double) this.adDelivered) * 100.0;
      if (ctr > 100.0) {
        ctr = 100.0;
      }
    }
    return ctr;
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
    return getCalculatedFillRate();
  }

  public void setFillRate(Double fillRate) {
    this.fillRate = fillRate;
  }

  public Double getCtr() {
    return getCalculatedCtr();
  }

  public void setCtr(Double ctr) {
    this.ctr = ctr;
  }

  public Boolean getOutboundRequest() {
    return outboundRequest;
  }

  public void setOutboundRequest(Boolean outboundRequest) {
    this.outboundRequest = outboundRequest;
  }

  public Long getBots() {
    return bots;
  }

  public void setBots(Long bots) {
    this.bots = bots;
  }

  public Long getFrauds() {
    return frauds;
  }

  public void setFrauds(Long frauds) {
    this.frauds = frauds;
  }

  public Long getSiteId() {
    return siteId;
  }

  public void setSiteId(Long siteId) {
    this.siteId = siteId;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public Long getAdSourceId() {
    return adSourceId;
  }

  public void setAdSourceId(Long adSourceId) {
    this.adSourceId = adSourceId;
  }

  public String getAdSource() {
    return adSource;
  }

  public void setAdSource(String adSource) {
    this.adSource = adSource;
  }

  public Long getAdTagId() {
    return adTagId;
  }

  public void setAdTagId(Long adTagId) {
    this.adTagId = adTagId;
  }

  public String getAdTag() {
    return adTag;
  }

  public void setAdTag(String adTag) {
    this.adTag = adTag;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getDeviceMake() {
    return deviceMake;
  }

  public void setDeviceMake(String deviceMake) {
    this.deviceMake = deviceMake;
  }

  public String getDeviceModel() {
    return deviceModel;
  }

  public void setDeviceModel(String deviceModel) {
    this.deviceModel = deviceModel;
  }

  public String getDeviceOS() {
    return deviceOS;
  }

  public void setDeviceOS(String deviceOS) {
    this.deviceOS = deviceOS;
  }

  public String getDeviceOSVersion() {
    return deviceOSVersion;
  }

  public void setDeviceOSVersion(String deviceOSVersion) {
    this.deviceOSVersion = deviceOSVersion;
  }

  public String getCarrier() {
    return carrier;
  }

  public void setCarrier(String carrier) {
    this.carrier = carrier;
  }

  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public Double getCpm() {
    return cpm;
  }

  public void setCpm(Double cpm) {
    this.cpm = cpm;
  }

  public Double getRevenue() {
    return revenue;
  }

  public void setRevenue(Double revenue) {
    this.revenue = revenue;
  }

  public Double getRpm() {
    return rpm;
  }

  public void setRpm(Double rpm) {
    this.rpm = rpm;
  }

  public Double getTotalRevenue() {
    return totalRevenue;
  }

  public void setTotalRevenue(Double totalRevenue) {
    this.totalRevenue = totalRevenue;
  }

  public Double getTotalrpm() {
    return totalrpm;
  }

  public void setTotalrpm(Double totalrpm) {
    this.totalrpm = totalrpm;
  }

  public Double getTotalEcpm() {
    return totalEcpm;
  }

  public void setTotalEcpm(Double totalEcpm) {
    this.totalEcpm = totalEcpm;
  }

  public Double getMmRevenue() {
    return mmRevenue;
  }

  public void setMmRevenue(Double mmRevenue) {
    this.mmRevenue = mmRevenue;
  }
}
