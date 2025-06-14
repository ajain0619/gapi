package com.nexage.dw.geneva.dashboard.model;

import java.math.BigDecimal;

public class BuyerDashboardSummary extends BaseBuyerMetrics {

  private static final long serialVersionUID = 1L;

  public BuyerDashboardSummary(
      long requests, long bidsReceived, long bidsWon, long delivered, BigDecimal revenue) {
    super(requests, bidsReceived, bidsWon, delivered, revenue);
  }
}
