package com.ssp.geneva.server.report.report.impl.finance.mediationandrtbrevenue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;
import com.ssp.geneva.server.report.report.util.MathUtil;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediationAndRTBRevenueResponse implements ReportResponse {

  private String customerId;
  private String customerName;
  private String customerType;
  private Double grossMargin;
  private Double grossRevenue;
  private Double netRevenue;
  private Double totalRevenue;

  public void roundValues() {
    this.grossMargin = MathUtil.roundDouble(grossMargin, 12);
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerType() {
    return customerType;
  }

  public void setCustomerType(String customerType) {
    this.customerType = customerType;
  }

  public Double getGrossMargin() {
    return grossMargin;
  }

  public void setGrossMargin(Double grossMargin) {
    this.grossMargin = grossMargin;
  }

  public Double getGrossRevenue() {
    return grossRevenue;
  }

  public void setGrossRevenue(Double grossRevenue) {
    this.grossRevenue = grossRevenue;
  }

  public Double getNetRevenue() {
    return netRevenue;
  }

  public void setNetRevenue(Double netRevenue) {
    this.netRevenue = netRevenue;
  }

  public Double getTotalRevenue() {
    return totalRevenue;
  }

  public void setTotalRevenue(Double totalRevenue) {
    this.totalRevenue = totalRevenue;
  }

  @Override
  public ResponseMetadata getResponseMetadata() {
    return null;
  }
}
