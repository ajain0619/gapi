package com.nexage.dw.geneva.dashboard.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class SellerDashboardDetail extends BaseSellerMetrics implements Serializable {

  private static final long serialVersionUID = 1L;

  private String date;

  public SellerDashboardDetail(
      String date, long requests, long served, long clicks, long displayed, BigDecimal revenue) {
    super(requests, served, clicks, displayed, revenue);
    this.date = date;
  }

  public String getDate() {
    return date;
  }
}
