package com.nexage.dw.geneva.dashboard.model;

public class NexageDashboardSummary extends BaseNexageMetrics {

  private static final long serialVersionUID = 1L;

  public NexageDashboardSummary(
      SellerDashboardSummary sellerMetrics, BuyerDashboardSummary buyerMetrics) {
    super(sellerMetrics, buyerMetrics);
  }
}
