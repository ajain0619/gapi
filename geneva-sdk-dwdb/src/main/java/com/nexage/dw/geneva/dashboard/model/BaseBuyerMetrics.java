package com.nexage.dw.geneva.dashboard.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class BaseBuyerMetrics implements Serializable {

  private static final long serialVersionUID = 1L;

  private long requests;

  private long bidsWon;

  private long bidsReceived;

  private long delivered;

  private BigDecimal revenue = new BigDecimal("0.0");

  private double bidRate;

  private double winRate;

  private BigDecimal eCpm = new BigDecimal("0.0");

  protected BaseBuyerMetrics(
      long requests, long bidsReceived, long bidsWon, long delivered, BigDecimal revenue) {
    this.requests = requests;
    this.bidsWon = bidsWon;
    this.delivered = delivered;
    this.revenue = revenue;

    if (requests > 0) this.bidRate = bidsReceived / (double) requests * 100;
    if (bidsReceived > 0) this.winRate = bidsWon / (double) bidsReceived * 100;
    if (revenue != null && delivered > 0)
      this.eCpm =
          revenue
              .divide(new BigDecimal(delivered), 8, RoundingMode.HALF_UP)
              .multiply(new BigDecimal(1000));
  }

  public long getRequests() {
    return requests;
  }

  public long getBidsWon() {
    return bidsWon;
  }

  public long getBidsReceived() {
    return bidsReceived;
  }

  public long getDelivered() {
    return delivered;
  }

  public BigDecimal getRevenue() {
    return revenue;
  }

  public double getBidRate() {
    return bidRate;
  }

  public double getWinRate() {
    return winRate;
  }

  public BigDecimal geteCpm() {
    return eCpm;
  }
}
