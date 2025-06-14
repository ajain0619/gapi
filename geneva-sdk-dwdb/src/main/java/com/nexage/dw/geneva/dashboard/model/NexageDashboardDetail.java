package com.nexage.dw.geneva.dashboard.model;

import java.io.Serializable;
import java.util.List;

public class NexageDashboardDetail implements Serializable {

  private static final long serialVersionUID = -2984118685604593952L;

  private final List<SellerDashboardDetail> sellerMetrics;

  private final List<BuyerDashboardDetail> buyerMetrics;

  public NexageDashboardDetail(
      List<SellerDashboardDetail> sellerMetrics, List<BuyerDashboardDetail> buyerMetrics) {
    this.buyerMetrics = buyerMetrics;
    this.sellerMetrics = sellerMetrics;
  }

  public List<SellerDashboardDetail> getSellerMetrics() {
    return sellerMetrics;
  }

  public List<BuyerDashboardDetail> getBuyerMetrics() {
    return buyerMetrics;
  }
}
