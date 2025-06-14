package com.ssp.geneva.server.report.report.impl.buyer.bidderspend;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;
import java.math.BigDecimal;
import java.math.RoundingMode;

@JsonInclude(Include.NON_NULL)
public class BidderSpendResponse implements ReportResponse {

  private Long adDelivered;
  private BigDecimal netAcquisitionCost;
  private BigDecimal grossAcquisitionCost;
  private Long bidderId;
  private String bidder;
  private String seat;
  private String dealID;
  private Long siteId;
  private String site;
  private String siteInternalName;
  private Long buyerId;
  private String buyer;
  private BigDecimal eCpm;
  private String interval;

  public BidderSpendResponse() {}

  public BidderSpendResponse(String interval) {
    this.adDelivered = 0L;
    this.netAcquisitionCost = BigDecimal.ZERO;
    this.grossAcquisitionCost = BigDecimal.ZERO;
    this.eCpm = BigDecimal.ZERO;
    this.interval = interval;
  }

  @Override
  public ResponseMetadata getResponseMetadata() {
    return null;
  }

  public void postInitialize() {
    if (adDelivered == null || netAcquisitionCost == null || grossAcquisitionCost == null) {
      return;
    }
    eCpm =
        this.adDelivered != 0
            ? this.netAcquisitionCost
                .divide(BigDecimal.valueOf(this.adDelivered), 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(1000))
            : BigDecimal.ZERO;
    this.grossAcquisitionCost =
        grossAcquisitionCost.divide(new BigDecimal("1000"), 8, RoundingMode.HALF_UP);
  }

  public Long getAdDelivered() {
    return adDelivered;
  }

  public void setAdDelivered(Long adDelivered) {
    this.adDelivered = adDelivered;
  }

  public BigDecimal getNetAcquisitionCost() {
    return netAcquisitionCost;
  }

  public void setNetAcquisitionCost(BigDecimal netAcquisitionCost) {
    this.netAcquisitionCost = netAcquisitionCost;
  }

  public BigDecimal getGrossAcquisitionCost() {
    return grossAcquisitionCost;
  }

  public void setGrossAcquisitionCost(BigDecimal grossAcquisitionCost) {
    this.grossAcquisitionCost = grossAcquisitionCost;
  }

  public Long getBidderId() {
    return bidderId;
  }

  public void setBidderId(Long bidderId) {
    this.bidderId = bidderId;
  }

  public String getBidder() {
    return bidder;
  }

  public void setBidder(String bidder) {
    this.bidder = bidder;
  }

  public String getSeat() {
    return seat;
  }

  public void setSeat(String seat) {
    this.seat = seat;
  }

  public String getDealID() {
    return dealID;
  }

  public void setDealID(String dealID) {
    this.dealID = dealID;
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

  public String getSiteInternalName() {
    return siteInternalName;
  }

  public void setSiteInternalName(String siteInternalName) {
    this.siteInternalName = siteInternalName;
  }

  public Long getBuyerId() {
    return buyerId;
  }

  public void setBuyerId(Long buyerId) {
    this.buyerId = buyerId;
  }

  public String getBuyer() {
    return buyer;
  }

  public void setBuyer(String buyer) {
    this.buyer = buyer;
  }

  public BigDecimal geteCpm() {
    return eCpm;
  }

  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }
}
