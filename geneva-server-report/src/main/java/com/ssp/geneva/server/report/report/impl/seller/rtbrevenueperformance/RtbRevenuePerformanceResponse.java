package com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RtbRevenuePerformanceResponse implements ReportResponse {
  private Long adServed;
  private Long adDelivered;
  private Long siteId;
  private String site;
  private String position;
  private Long adTagId;
  private String adTag;
  private String country;
  private Long bidderId;
  private String bidder;
  private Long buyerId;
  private String buyer;
  private String rtbAdvertiserDomain;
  private String seat;
  private String dealID;
  private String deal;
  private Double revenue;
  private Double mmRevenue;
  private Double totalRevenue;
  private String interval;
  private String positionMemo;
  private Long s2sHbPartnerPid;
  private String s2sHbPartnerName;
  private String s2sHbPartner;

  public RtbRevenuePerformanceResponse(String interval) {
    this.adServed = 0L;
    this.adDelivered = 0L;
    this.revenue = 0d;
    this.mmRevenue = 0d;
    this.totalRevenue = 0d;
    this.interval = interval;
  }

  public void postInitialize() {
    if (this.mmRevenue == null) {
      this.mmRevenue = 0d;
    }
    if (this.revenue == null) {
      this.revenue = 0d;
    }
    this.revenue = totalRevenue - mmRevenue;
  }

  @Override
  public ResponseMetadata getResponseMetadata() {
    return null;
  }
}
