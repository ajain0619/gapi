package com.nexage.dw.geneva.dashboard.model;

import java.io.Serializable;

public abstract class BaseNexageMetrics implements Serializable {

  private static final long serialVersionUID = 1L;

  private final SellerDashboardSummary sellerMetrics;

  private final BuyerDashboardSummary buyerMetrics;

  protected BaseNexageMetrics(
      SellerDashboardSummary sellerMetrics, BuyerDashboardSummary buyerMetrics) {
    this.buyerMetrics = buyerMetrics;
    this.sellerMetrics = sellerMetrics;
  }

  public SellerDashboardSummary getSellerMetrics() {
    return sellerMetrics;
  }

  public BuyerDashboardSummary getBuyerMetrics() {
    return buyerMetrics;
  }
}
