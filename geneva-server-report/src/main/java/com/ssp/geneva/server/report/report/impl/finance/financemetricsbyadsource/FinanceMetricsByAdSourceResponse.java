package com.ssp.geneva.server.report.report.impl.finance.financemetricsbyadsource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;

@JsonInclude(Include.NON_NULL)
public class FinanceMetricsByAdSourceResponse implements ReportResponse {

  private String adSource;
  private long adSourceId;
  private long adRequests;
  private long adServed;
  private long adDelivered;
  private long adClicks;
  private double totalRevenue;
  private double grossRevenue;
  private double netRevenue;
  private double grossMargin;
  private double costOfSales;
  private double fillRate;
  private double cpm;
  private double rpm;
  private double ctr;

  @Override
  public ResponseMetadata getResponseMetadata() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getAdSource() {
    return adSource;
  }

  public void setAdSource(String adSource) {
    this.adSource = adSource;
  }

  public long getAdSourceId() {
    return adSourceId;
  }

  public void setAdSourceId(long adSourceId) {
    this.adSourceId = adSourceId;
  }

  public long getAdRequests() {
    return adRequests;
  }

  public void setAdRequests(long adRequests) {
    this.adRequests = adRequests;
  }

  public long getAdServed() {
    return adServed;
  }

  public void setAdServed(long adServed) {
    this.adServed = adServed;
  }

  public long getAdDelivered() {
    return adDelivered;
  }

  public void setAdDelivered(long adDelivered) {
    this.adDelivered = adDelivered;
  }

  public long getAdClicks() {
    return adClicks;
  }

  public void setAdClicks(long adClicks) {
    this.adClicks = adClicks;
  }

  public double getTotalRevenue() {
    return totalRevenue;
  }

  public void setTotalRevenue(double totalRevenue) {
    this.totalRevenue = totalRevenue;
  }

  public double getGrossRevenue() {
    return grossRevenue;
  }

  public void setGrossRevenue(double grossRevenue) {
    this.grossRevenue = grossRevenue;
  }

  public double getNetRevenue() {
    return netRevenue;
  }

  public void setNetRevenue(double netRevenue) {
    this.netRevenue = netRevenue;
  }

  public double getGrossMargin() {
    return grossMargin;
  }

  public void setGrossMargin(double grossMargin) {
    this.grossMargin = grossMargin;
  }

  public double getCostOfSales() {
    return costOfSales;
  }

  public void setCostOfSales(double costOfSales) {
    this.costOfSales = costOfSales;
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

  public void setFillRate(double fillRate) {
    this.fillRate = fillRate;
  }

  public double getCpm() {
    if (this.adDelivered > 0L) {
      this.cpm = this.totalRevenue / (double) this.adDelivered * 1000.0D;
    }
    return cpm;
  }

  public void setCpm(double cpm) {
    this.cpm = cpm;
  }

  public double getRpm() {
    if (this.adRequests > 0L) {
      this.rpm = this.totalRevenue / (double) this.adRequests * 1000.0D;
    }
    return rpm;
  }

  public void setRpm(double rpm) {
    this.rpm = rpm;
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

  public void setCtr(double ctr) {
    this.ctr = ctr;
  }
}
