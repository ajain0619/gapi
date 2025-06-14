package com.ssp.geneva.server.report.report.impl.seller.revenue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexage.admin.core.enums.PlacementCategory;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;
import com.ssp.geneva.server.report.report.util.MathUtil;
import org.apache.commons.lang3.StringUtils;

@JsonInclude(Include.NON_NULL)
public class SellerRevenueResponse implements ReportResponse {

  private Long adRequests;
  private Long adServed;
  private Long adDelivered;
  private Long adClicks;
  private Double fillRate;
  private Double ctr;
  private Double revenue;
  private Double cpm;
  private Double rpm;
  private Double totalRevenue;
  private Double totalEcpm;

  @JsonProperty(value = "totalrpm")
  private Double totalRpm;

  private Double mmRevenue;
  private Boolean outboundRequest = false;

  // dim=seller
  private Long sellerId;
  // dim=site && dim=seller && dim=tag
  private String seller;
  // dim=site
  private Long siteId;
  // dim=tag
  private Long adTagId;
  private String adTag;
  private String buyer;
  // dim=site && dim=tag
  private String siteType;
  private String site;
  // dim=adsource
  private Long adSourceId;
  private String adSource;
  // dim=position && dim=tag
  private String position;
  private String placementType;
  private String positionMemo;
  // dim=country
  private String country;
  // dim=adSourceTypeId
  private Long adSourceTypeId;
  private String sourceType;
  // dim=day, week, month
  private String interval;
  // dim=headerBidding
  private Long hbId;
  private String headerBidding;

  public SellerRevenueResponse() {}

  public SellerRevenueResponse(String interval) {
    this.interval = interval;
    this.adClicks = 0L;
    this.adDelivered = 0L;
    this.adRequests = 0L;
    this.adServed = 0L;
    this.cpm = 0.0;
    this.ctr = 0.0;
    this.fillRate = 0.0;
    this.mmRevenue = 0.0;
    this.revenue = 0.0;
    this.rpm = 0.0;
    this.totalEcpm = 0.0;
    this.totalRevenue = 0.0;
    this.totalRpm = 0.0;
  }

  public void roundValues() {
    this.ctr = MathUtil.roundDouble(ctr, 4);
    this.fillRate = MathUtil.roundDouble(fillRate, 4);
    this.cpm = MathUtil.roundDouble(cpm, 12);
    this.totalEcpm = MathUtil.roundDouble(totalEcpm, 12);
    this.rpm = MathUtil.roundDouble(rpm, 12);
    this.totalRpm = MathUtil.roundDouble(totalRpm, 12);
  }

  @Override
  public ResponseMetadata getResponseMetadata() {
    // TODO Auto-generated method stub
    return null;
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

  public Double getTotalRpm() {
    return totalRpm;
  }

  public void setTotalRpm(Double totalRpm) {
    this.totalRpm = totalRpm;
  }

  public Double getMmRevenue() {
    return mmRevenue;
  }

  public void setMmRevenue(Double mmRevenue) {
    this.mmRevenue = mmRevenue;
  }

  public Boolean getOutboundRequest() {
    return outboundRequest;
  }

  public void setOutboundRequest(Boolean outboundRequest) {
    this.outboundRequest = outboundRequest;
  }

  public Long getSellerId() {
    return sellerId;
  }

  public void setSellerId(Long sellerId) {
    this.sellerId = sellerId;
  }

  public String getSeller() {
    return seller;
  }

  public void setSeller(String seller) {
    this.seller = seller;
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

  public String getAdTag() {
    return adTag;
  }

  public void setAdTag(String adTag) {
    this.adTag = adTag;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public String getBuyer() {
    return buyer;
  }

  public void setBuyer(String buyer) {
    this.buyer = buyer;
  }

  public String getSiteType() {
    return siteType;
  }

  public void setSiteType(String siteType) {
    this.siteType = siteType;
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

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getPositionMemo() {
    return positionMemo;
  }

  public void setPositionMemo(String positionMemo) {
    this.positionMemo = positionMemo;
  }

  public String getPlacementType() {
    if (!StringUtils.isEmpty(this.placementType)) {
      return PlacementCategory.fromInt(Integer.parseInt(this.placementType)).name();
    }
    return null;
  }

  public void setPlacementType(String placementType) {
    this.placementType = placementType;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Long getAdSourceTypeId() {
    return adSourceTypeId;
  }

  public void setAdSourceTypeId(Long adSourceTypeId) {
    this.adSourceTypeId = adSourceTypeId;
  }

  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public String getHeaderBidding() {
    return headerBidding;
  }

  public void setHeaderBidding(String headerBidding) {
    this.headerBidding = headerBidding;
  }

  public Long getHbId() {
    return hbId;
  }

  public void setHbId(Long hbId) {
    this.hbId = hbId;
  }
}
