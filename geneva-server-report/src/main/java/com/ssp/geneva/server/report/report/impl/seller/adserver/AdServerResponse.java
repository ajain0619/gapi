package com.ssp.geneva.server.report.report.impl.seller.adserver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdServerResponse implements ReportResponse {

  private Long adServed;
  private Long adDelivered;
  private Long adClicks;
  private Double fillRate;
  private Double revenue;
  private Double ctr;
  private Double cpm;

  public AdServerResponse() {}

  public AdServerResponse(String interval) {
    this.adServed = 0L;
    this.adDelivered = 0L;
    this.adClicks = 0L;
    this.fillRate = 0.0;
    this.ctr = 0.0;
    this.cpm = 0.0;
    this.revenue = 0.0;
    this.interval = interval;
  }

  // dim
  private String site;
  private Long siteId;
  private String position;
  private String adTag;
  private Long adTagId;
  private String advertiser;
  private Long advertiserId;
  private String campaignType;
  private Long campaignTypeId;
  private String campaign;
  private Long campaignId;
  private String creative;
  private Long creativeId;
  // dim=hour, day, week, month
  private String interval;

  // unused fields
  private Double rpm = 0.0;
  private Double totalRevenue = 0.0;
  private Double totalrpm = 0.0;
  private Double totalEcpm = 0.0;
  private Double mmRevenue = 0.0;
  private Long adRequests = 0L;

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

  private Double getCalculatedCpm() {
    Double cpm = 0.0;
    if (this.adDelivered != null && this.adDelivered > 0 && this.adClicks != null) {
      cpm = (this.revenue / (double) this.adDelivered) * 1000.0;
    }
    return cpm;
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

  public Double getRevenue() {
    return revenue;
  }

  public void setRevenue(Double revenue) {
    this.revenue = revenue;
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

  public Double getCpm() {
    return getCalculatedCpm();
  }

  public void setCpm(Double cpm) {
    this.cpm = cpm;
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

  public String getAdTag() {
    return adTag;
  }

  public void setAdTag(String adTag) {
    this.adTag = adTag;
  }

  public String getAdvertiser() {
    return advertiser;
  }

  public void setAdvertiser(String advertiser) {
    this.advertiser = advertiser;
  }

  public String getCampaignType() {
    return campaignType;
  }

  public void setCampaignType(String campaignType) {
    this.campaignType = campaignType;
  }

  public String getCampaign() {
    return campaign;
  }

  public void setCampaign(String campaign) {
    this.campaign = campaign;
  }

  public String getCreative() {
    return creative;
  }

  public void setCreative(String creative) {
    this.creative = creative;
  }

  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public Long getSiteId() {
    return siteId;
  }

  public void setSiteId(Long siteId) {
    this.siteId = siteId;
  }

  public Long getAdTagId() {
    return adTagId;
  }

  public void setAdTagId(Long adTagId) {
    this.adTagId = adTagId;
  }

  public Long getCampaignTypeId() {
    return campaignTypeId;
  }

  public void setCampaignTypeId(Long campaignTypeId) {
    this.campaignTypeId = campaignTypeId;
  }

  public Long getCampaignId() {
    return campaignId;
  }

  public void setCampaignId(Long campaignId) {
    this.campaignId = campaignId;
  }

  public Long getCreativeId() {
    return creativeId;
  }

  public void setCreativeId(Long creativeId) {
    this.creativeId = creativeId;
  }

  public Long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }

  @Override
  public ResponseMetadata getResponseMetadata() {
    return null;
  }
}
