package com.ssp.geneva.server.report.report.impl.internal.sitedistribution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;
import java.math.BigDecimal;
import java.math.RoundingMode;

@JsonInclude(Include.NON_NULL)
public class RtbSiteDistributionResponse implements ReportResponse {

  private String site;
  private long adServed;
  private long adDelivered;
  private BigDecimal grossAcquisitionCost;
  private BigDecimal netAcquisitionCost;
  @JsonIgnore private BigDecimal grossAcquisitionCostInCpm;

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public BigDecimal getGrossAcquisitionCostInCpm() {
    return grossAcquisitionCostInCpm;
  }

  public void setGrossAcquisitionCostInCpm(BigDecimal grossAcquisitionCostInCpm) {
    this.grossAcquisitionCostInCpm = grossAcquisitionCostInCpm;
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

  public BigDecimal getGrossAcquisitionCost() {
    this.grossAcquisitionCost =
        grossAcquisitionCostInCpm != null
            ? grossAcquisitionCostInCpm.divide(new BigDecimal("1000"), 8, RoundingMode.HALF_UP)
            : null;
    return grossAcquisitionCost;
  }

  public void setGrossAcquisitionCost(BigDecimal grossAcquisitionCost) {
    this.grossAcquisitionCost = grossAcquisitionCost;
  }

  public BigDecimal getNetAcquisitionCost() {
    return netAcquisitionCost;
  }

  public void setNetAcquisitionCost(BigDecimal netAcquisitionCost) {
    this.netAcquisitionCost = netAcquisitionCost;
  }

  @Override
  public ResponseMetadata getResponseMetadata() {
    // TODO Auto-generated method stub
    return null;
  }
}
