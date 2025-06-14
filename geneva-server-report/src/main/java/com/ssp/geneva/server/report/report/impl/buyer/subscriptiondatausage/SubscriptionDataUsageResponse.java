package com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage;

import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;

public class SubscriptionDataUsageResponse implements ReportResponse {
  private String provider;
  private Long providerRequests;
  private String bidder;
  private Long providerId;
  private Long bidderId;
  private Long overallBidRequests;
  private Double requestRate;
  private Long bidReceived;
  private Double bidRate;
  private Long wins;
  private Double winRate;

  public void postInitialize() {
    this.requestRate = 0d;
    this.bidRate = 0d;
    this.winRate = 0d;
    if (this.overallBidRequests > 0) {
      this.requestRate = 100 * (this.providerRequests / (double) this.overallBidRequests);
      this.bidRate = 100 * (this.bidReceived / (double) this.overallBidRequests);
    }
    if (this.bidReceived > 0) {
      this.winRate = 100 * (this.wins / (double) this.bidReceived);
    }
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public Long getProviderRequests() {
    return providerRequests;
  }

  public void setProviderRequests(Long providerRequests) {
    this.providerRequests = providerRequests;
  }

  public String getBidder() {
    return bidder;
  }

  public void setBidder(String bidder) {
    this.bidder = bidder;
  }

  public Long getProviderId() {
    return providerId;
  }

  public void setProviderId(Long providerId) {
    this.providerId = providerId;
  }

  public Long getBidderId() {
    return bidderId;
  }

  public void setBidderId(Long bidderId) {
    this.bidderId = bidderId;
  }

  public Long getOverallBidRequests() {
    return overallBidRequests;
  }

  public void setOverallBidRequests(Long overallBidRequests) {
    this.overallBidRequests = overallBidRequests;
  }

  public Double getRequestRate() {
    return requestRate;
  }

  public void setRequestRate(Double requestRate) {
    this.requestRate = requestRate;
  }

  public Long getBidReceived() {
    return bidReceived;
  }

  public void setBidReceived(Long bidReceived) {
    this.bidReceived = bidReceived;
  }

  public Double getBidRate() {
    return bidRate;
  }

  public void setBidRate(Double bidRate) {
    this.bidRate = bidRate;
  }

  public Long getWins() {
    return wins;
  }

  public void setWins(Long wins) {
    this.wins = wins;
  }

  public Double getWinRate() {
    return winRate;
  }

  public void setWinRate(Double winRate) {
    this.winRate = winRate;
  }

  @Override
  public ResponseMetadata getResponseMetadata() {
    return null;
  }
}
