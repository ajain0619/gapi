package com.ssp.geneva.server.report.report.impl.internal.rxperformance;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** DTO for one row of RX_PERFORMANCE report. */
public class RxPerformanceResponse implements ReportResponse {

  private String auctionType;
  private long adRequests;
  private long auctions;
  private long adServed;
  private long noBidders;
  private long insufficientBids;
  private long noBids;
  private long maxEligibleBids;
  private double avgEligibleBids;
  private BigDecimal maxCpm = BigDecimal.ZERO;
  private BigDecimal avgCpm = BigDecimal.ZERO;

  @JsonIgnore private long biddersSum;

  @JsonIgnore private BigDecimal priceSum;

  @JsonIgnore private long bidsReceived;

  @Override
  public ResponseMetadata getResponseMetadata() {
    // TODO Auto-generated method stub
    return null;
  }

  @JsonGetter
  public Double getAvgEligibleBids() {
    return auctions > 0 ? bidsReceived / (double) auctions : 0.0;
  }

  @JsonGetter
  public BigDecimal getAvgCpm() {
    BigDecimal avgCpm = BigDecimal.ZERO;
    if (adServed > 0) {
      avgCpm = priceSum.divide(new BigDecimal(adServed), 8, RoundingMode.HALF_UP);
    }
    return avgCpm;
  }

  public String getAuctionType() {
    return auctionType;
  }

  public void setAuctionType(String auctionType) {
    this.auctionType = auctionType;
  }

  public long getAdRequests() {
    return adRequests;
  }

  public void setAdRequests(long adRequests) {
    this.adRequests = adRequests;
  }

  public long getAuctions() {
    return auctions;
  }

  public void setAuctions(long auctions) {
    this.auctions = auctions;
  }

  public long getAdServed() {
    return adServed;
  }

  public void setAdServed(long adServed) {
    this.adServed = adServed;
  }

  public long getNoBidders() {
    return noBidders;
  }

  public void setNoBidders(long noBidders) {
    this.noBidders = noBidders;
  }

  public long getInsufficientBids() {
    return insufficientBids;
  }

  public void setInsufficientBids(long insufficientBids) {
    this.insufficientBids = insufficientBids;
  }

  public long getNoBids() {
    return noBids;
  }

  public void setNoBids(long noBids) {
    this.noBids = noBids;
  }

  public long getMaxEligibleBids() {
    return maxEligibleBids;
  }

  public void setMaxEligibleBids(long maxEligibleBids) {
    this.maxEligibleBids = maxEligibleBids;
  }

  public void setAvgEligibleBids(double avgEligibleBids) {
    this.avgEligibleBids = avgEligibleBids;
  }

  public BigDecimal getMaxCpm() {
    return maxCpm;
  }

  public void setMaxCpm(BigDecimal maxCpm) {
    this.maxCpm = maxCpm;
  }

  public void setAvgCpm(BigDecimal avgCpm) {
    this.avgCpm = avgCpm;
  }

  public long getBiddersSum() {
    return biddersSum;
  }

  public void setBiddersSum(long biddersSum) {
    this.biddersSum = biddersSum;
  }

  public BigDecimal getPriceSum() {
    return priceSum;
  }

  public void setPriceSum(BigDecimal priceSum) {
    this.priceSum = priceSum;
  }

  public long getBidsReceived() {
    return bidsReceived;
  }

  public void setBidsReceived(long bidsReceived) {
    this.bidsReceived = bidsReceived;
  }
}
