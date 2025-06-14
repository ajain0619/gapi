package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BasePubSelfServeMetricsDTO {

  protected long requests;
  protected long served;
  protected long delivered;
  @JsonIgnore protected long clicks;
  protected double revenue;
  protected double fillrate;
  protected double ctr;
  protected double rpm;
  protected double ecpm;
  @JsonIgnore protected double preRevenue;
  protected double revenueTrendPercent;

  public long getRequests() {
    return requests;
  }

  public long getServed() {
    return served;
  }

  public long getDelivered() {
    return delivered;
  }

  public double getRevenue() {
    return revenue;
  }

  public double getFillrate() {
    if (requests > 0) fillrate = (served / (double) requests) * 100;
    if (fillrate > 100) fillrate = 100;
    return fillrate;
  }

  public double getCtr() {
    if (delivered > 0) ctr = (clicks / (double) delivered) * 100;
    if (ctr > 100) ctr = 100;
    return ctr;
  }

  public double getRpm() {
    if (requests > 0) rpm = (revenue / (double) requests) * 1000;
    return rpm;
  }

  public double getEcpm() {
    if (delivered > 0) ecpm = (revenue / (double) delivered) * 1000;
    return ecpm;
  }

  public double getRevenueTrendPercent() {
    if (preRevenue > 0) revenueTrendPercent = (revenue - preRevenue) / preRevenue * 100;
    return revenueTrendPercent;
  }
}
