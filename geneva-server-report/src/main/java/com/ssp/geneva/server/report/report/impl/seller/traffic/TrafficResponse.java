package com.ssp.geneva.server.report.report.impl.seller.traffic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Response DTO for one report entry of Traffic report. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TrafficResponse implements ReportResponse {

  @Override
  public ResponseMetadata getResponseMetadata() {
    return null; // TODO implement
  }

  private Long adRequests;
  private Long adServed;
  private Long adDelivered;
  private Long adClicks;
  private Boolean outboundRequest = false;
  private Long bots;
  private Long frauds;
  private Long siteId; // dim = site
  private String site;
  private String position; // dim = position
  private Long adSourceId; // dim = adsource
  private String adSource;
  private Long adTagId; // dim = adtag
  private String adTag;
  private String interval; // dim = hour, day, week, month
  private Long hbId; // dim = headerBidding
  private String headerBidding;
  private String s2sHbPartner; // dim = s2sHbPartner
  private Long s2sHbPartnerPid;
  private String s2sHbPartnerName;
  private Double cpm = 0.0;
  private Double revenue = 0.0;
  private Double rpm = 0.0;
  private Double totalRevenue;
  private Double totalrpm;
  private Double totalEcpm;
  private Double mmRevenue;
  private String positionMemo;

  @Getter(AccessLevel.NONE)
  private Double fillRate;

  @Getter(AccessLevel.NONE)
  private Double ctr;

  public TrafficResponse(String date) {
    this.interval = date;
    this.adRequests = 0L;
    this.adServed = 0L;
    this.adDelivered = 0L;
    this.adClicks = 0L;
    this.fillRate = 0.0;
    this.ctr = 0.0;
    this.bots = 0L;
    this.frauds = 0L;
    this.totalRevenue = 0.0;
    this.totalrpm = 0.0;
    this.totalEcpm = 0.0;
    this.mmRevenue = 0.0;
  }

  private Double getCalculatedFillRate() {
    double fillRate = 0.0;
    if (this.adRequests != null && this.adRequests > 0 && this.adServed != null) {
      fillRate = (this.adServed / (double) this.adRequests) * 100.0;
      if (fillRate > 100.0) {
        fillRate = 100.0;
      }
    }
    return fillRate;
  }

  private Double getCalculatedCtr() {
    double ctr = 0.0;
    if (this.adDelivered != null && this.adDelivered > 0 && this.adClicks != null) {
      ctr = (this.adClicks / (double) this.adDelivered) * 100.0;
      if (ctr > 100.0) {
        ctr = 100.0;
      }
    }
    return ctr;
  }

  public Double getFillRate() {
    return getCalculatedFillRate();
  }

  public Double getCtr() {
    return getCalculatedCtr();
  }
}
