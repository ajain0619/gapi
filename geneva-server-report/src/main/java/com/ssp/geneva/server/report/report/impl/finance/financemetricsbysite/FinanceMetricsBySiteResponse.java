package com.ssp.geneva.server.report.report.impl.finance.financemetricsbysite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class FinanceMetricsBySiteResponse implements ReportResponse {

  private String publisher;
  private String site;
  private long siteId;
  private long publisherId;
  private String hbPartnerName;
  private long adRequests;
  private long adServed;
  private long adDelivered;
  private long adClicks;
  private double totalRevenue;
  private double totalRevenueRtb;
  private double totalRevenueMediation;
  private double grossRevenue;
  private double grossRevenueRtb;
  private double grossRevenueMediation;
  private double netRevenue;
  private double netRevenueRtb;
  private double netRevenueMediation;
  private double costOfSales;
  private double costOfSalesRtb;
  private double costOfSalesMediation;
  private double grossMargin;

  @Getter(AccessLevel.NONE)
  private double fillRate;

  @Getter(AccessLevel.NONE)
  private double cpm;

  @Getter(AccessLevel.NONE)
  private double rpm;

  @Getter(AccessLevel.NONE)
  private double ctr;

  @Override
  public ResponseMetadata getResponseMetadata() {
    // TODO Auto-generated method stub
    return null;
  }

  public double getFillRate() {
    if (this.adRequests > 0L) {
      this.fillRate = (double) this.adServed / (double) this.adRequests * 100.0D;
      if (this.fillRate > 100.0D) {
        this.fillRate = 100.0D;
      }
    }
    return fillRate;
  }

  public double getCpm() {
    if (this.adDelivered > 0L) {
      this.cpm = this.totalRevenue / (double) this.adDelivered * 1000.0D;
    }
    return cpm;
  }

  public double getRpm() {
    if (this.adRequests > 0L) {
      this.rpm = this.totalRevenue / (double) this.adRequests * 1000.0D;
    }
    return rpm;
  }

  public double getCtr() {
    if (this.adDelivered > 0L) {
      this.ctr = (double) this.adClicks / (double) this.adDelivered * 100.0D;
      if (this.ctr > 100.0D) {
        this.ctr = 100.0D;
      }
    }
    return ctr;
  }
}
