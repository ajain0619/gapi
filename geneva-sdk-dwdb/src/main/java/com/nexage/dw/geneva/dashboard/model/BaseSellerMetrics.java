package com.nexage.dw.geneva.dashboard.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class BaseSellerMetrics implements Serializable {

  private static final long serialVersionUID = 1L;

  private long requests;

  private long served;

  private long displayed;

  private long clicks;

  private double fillRate;

  private double ctr;

  private BigDecimal revenue = new BigDecimal("0.0");

  private BigDecimal eCpm = new BigDecimal("0.0");

  private BigDecimal rpm = new BigDecimal("0.0");

  protected BaseSellerMetrics(
      long requests, long served, long clicks, long displayed, BigDecimal revenue) {
    this.requests = requests;
    this.served = served;
    this.clicks = clicks;
    this.displayed = displayed;
    this.revenue = revenue;
    if (this.requests > 0) {
      this.fillRate = (this.served / (double) this.requests) * 100;
      this.rpm =
          revenue
              .divide(new BigDecimal(requests), 8, RoundingMode.HALF_UP)
              .multiply(new BigDecimal(1000));
    }
    if (this.served > 0) {
      this.ctr = (this.clicks / (double) this.displayed) * 100;
    }
    if (this.displayed > 0) {
      this.eCpm =
          revenue
              .divide(new BigDecimal(displayed), 8, RoundingMode.HALF_UP)
              .multiply(new BigDecimal(1000));
    }
  }

  public long getRequests() {
    return requests;
  }

  public long getServed() {
    return served;
  }

  public long getDisplayed() {
    return displayed;
  }

  public long getClicks() {
    return clicks;
  }

  public double getFillRate() {
    return fillRate;
  }

  public double getCtr() {
    return ctr;
  }

  public BigDecimal getRevenue() {
    return revenue;
  }

  public BigDecimal geteCpm() {
    return eCpm;
  }

  public BigDecimal getRpm() {
    return rpm;
  }
}
