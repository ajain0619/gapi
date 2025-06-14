package com.ssp.geneva.server.report.performance.pss.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@JsonInclude(Include.NON_NULL)
public class BiddersPerformanceForPubSelfServe implements Serializable {

  private static final long serialVersionUID = 4313990501301570650L;

  private long bidRequests; // bidRequests

  private long bidReceived; // Bid Responses

  private double bidRate = 0; // bidRate

  private long bids; // bids

  private long wins; // bids won

  private double winRate = -1; // win rate

  private long adDelivered; // ads delivered

  @JsonIgnore private BigDecimal grossAcquisitionCost = BigDecimal.ZERO; // gross wins

  @JsonProperty("averageClearingPrice_net")
  private BigDecimal averageClearingPrice = BigDecimal.ZERO; // Avg Clearing Price (In Net)

  @JsonProperty("spend")
  private BigDecimal netAcquisitionCost = BigDecimal.ZERO; // spend //net wins

  @JsonProperty("eCpm_Net")
  private BigDecimal eCpm = BigDecimal.ZERO; // ecpm in net

  private Long bidderId;

  private String bidder;

  public BiddersPerformanceForPubSelfServe() {}

  public BiddersPerformanceForPubSelfServe(
      long bidRequests,
      long bidReceived,
      long numOfWins,
      long adDelivered,
      BigDecimal grossAcquisitionCostInCpm,
      BigDecimal netAcquisitionCost,
      long bids,
      long bidderId,
      String bidder) {
    this.bidRequests = bidRequests;
    this.bidReceived = bidReceived;
    this.bidRate =
        (this.bidRequests > 0) ? 100 * (this.bidReceived / (double) this.bidRequests) : 0.0;
    this.wins = numOfWins;
    this.winRate = (this.bidReceived > 0) ? 100 * (this.wins / (double) this.bidReceived) : 0.0;
    this.adDelivered = adDelivered;
    this.netAcquisitionCost = netAcquisitionCost;
    this.grossAcquisitionCost =
        grossAcquisitionCostInCpm != null
            ? grossAcquisitionCostInCpm.divide(new BigDecimal("1000"), 8, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
    this.averageClearingPrice =
        this.wins != 0
            ? (this.grossAcquisitionCost
                .divide(BigDecimal.valueOf(this.wins), 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(1000)))
            : BigDecimal.ZERO;
    this.eCpm =
        this.adDelivered != 0
            ? (this.netAcquisitionCost
                .divide(BigDecimal.valueOf(this.adDelivered), 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(1000)))
            : BigDecimal.ZERO;
    this.bids = bids;
    this.bidderId = bidderId;
    this.bidder = bidder;
  }

  public long getBidRequests() {
    return bidRequests;
  }

  public void setBidRequests(long bidRequests) {
    this.bidRequests = bidRequests;
  }

  public long getBidReceived() {
    return bidReceived;
  }

  public void setBidReceived(long bidReceived) {
    this.bidReceived = bidReceived;
  }

  public double getBidRate() {
    if (bidRequests == -1 && bidReceived == -1) // drill down by seat
    bidRate = -1.0;

    return bidRate;
  }

  public void setBidRate(double bidRate) {
    this.bidRate = bidRate;
  }

  public long getWins() {
    return wins;
  }

  public void setWins(long wins) {
    this.wins = wins;
  }

  public double getWinRate() {
    if (bidRequests != -1 && bidReceived != -1) {
      winRate = (bidReceived > 0) ? 100 * (wins / (double) bidReceived) : 0.0;
    } else {
      winRate = (bids > 0) ? 100 * (wins / (double) bids) : 0.0;
    }
    return winRate;
  }

  public void setWinRate(double winRate) {
    this.winRate = winRate;
  }

  public long getAdDelivered() {
    return adDelivered;
  }

  public void setAdDelivered(long adDelivered) {
    this.adDelivered = adDelivered;
  }

  @JsonIgnore()
  public BigDecimal getGrossAcquisitionCost() {
    return grossAcquisitionCost;
  }

  public void setGrossAcquisitionCost(BigDecimal grossAcquisitionCost) {
    this.grossAcquisitionCost = grossAcquisitionCost;
  }

  public BigDecimal getAverageClearingPrice() {
    return averageClearingPrice;
  }

  public void setAverageClearingPrice(BigDecimal averageClearingPrice) {
    this.averageClearingPrice = averageClearingPrice;
  }

  public BigDecimal getNetAcquisitionCost() {
    return netAcquisitionCost;
  }

  public void setNetAcquisitionCost(BigDecimal netAcquisitionCost) {
    this.netAcquisitionCost = netAcquisitionCost;
  }

  public BigDecimal geteCpm() {
    return eCpm;
  }

  public void seteCpm(BigDecimal eCpm) {
    this.eCpm = eCpm;
  }

  public long getBids() {
    return bids;
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

  public void setBids(long bids) {
    this.bids = bids;
  }
}
