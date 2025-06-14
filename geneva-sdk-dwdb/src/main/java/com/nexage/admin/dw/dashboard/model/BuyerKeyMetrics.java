package com.nexage.admin.dw.dashboard.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BuyerKeyMetrics implements Serializable {

  private static final long serialVersionUID = -8398178946066664823L;

  protected long bidsRequests;

  protected long adsDelivered;

  protected BigDecimal revenue = new BigDecimal("0.0");

  protected double bidRate;

  protected double winRate;

  protected double fillRate;

  protected BigDecimal eCpm = new BigDecimal("0.0");

  public BuyerKeyMetrics(
      long numOfAuctions,
      long bidsRequests,
      long bidsReceived,
      long bidsWon,
      long adsDelivered,
      BigDecimal revenue) {
    this.bidsRequests = bidsRequests;
    this.adsDelivered = adsDelivered;
    this.revenue = revenue;

    if (numOfAuctions > 0) this.fillRate = bidsRequests / (double) numOfAuctions * 100;
    if (bidsRequests > 0) this.bidRate = bidsReceived / (double) bidsRequests * 100;
    if (bidsReceived > 0) this.winRate = bidsWon / (double) bidsReceived * 100;
    if (revenue != null && adsDelivered > 0)
      this.eCpm =
          revenue
              .divide(new BigDecimal(adsDelivered), 8, RoundingMode.HALF_UP)
              .multiply(new BigDecimal(1000));
  }

  public long getBidsRequests() {
    return bidsRequests;
  }

  public void setBidsRequests(long bidsRequests) {
    this.bidsRequests = bidsRequests;
  }

  public long getAdsDelivered() {
    return adsDelivered;
  }

  public void setAdsDelivered(long adsDelivered) {
    this.adsDelivered = adsDelivered;
  }

  public BigDecimal getRevenue() {
    return revenue;
  }

  public void setRevenue(BigDecimal revenue) {
    this.revenue = revenue;
  }

  public double getBidRate() {
    return bidRate;
  }

  public void setBidRate(double bidRate) {
    this.bidRate = bidRate;
  }

  public double getWinRate() {
    return winRate;
  }

  public void setWinRate(double winRate) {
    this.winRate = winRate;
  }

  public double getFillRate() {
    return fillRate;
  }

  public void setFillRate(double fillRate) {
    this.fillRate = fillRate;
  }

  public BigDecimal geteCpm() {
    return eCpm;
  }

  public void seteCpm(BigDecimal eCpm) {
    this.eCpm = eCpm;
  }
}
