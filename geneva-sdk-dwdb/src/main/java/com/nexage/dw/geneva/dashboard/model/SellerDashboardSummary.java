package com.nexage.dw.geneva.dashboard.model;

import java.math.BigDecimal;

public class SellerDashboardSummary extends BaseSellerMetrics {

  private static final long serialVersionUID = 1L;

  public SellerDashboardSummary(
      long requests, long served, long clicks, long displayed, BigDecimal revenue) {
    super(requests, served, clicks, displayed, revenue);
  }
}
