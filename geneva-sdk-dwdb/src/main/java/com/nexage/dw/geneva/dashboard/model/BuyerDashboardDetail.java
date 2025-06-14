package com.nexage.dw.geneva.dashboard.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class BuyerDashboardDetail extends BaseBuyerMetrics implements Serializable {

  private static final long serialVersionUID = 1L;

  private String date;

  public BuyerDashboardDetail(
      String date,
      long requests,
      long bidsReceived,
      long bidsWon,
      long delivered,
      BigDecimal revenue) {
    super(requests, bidsReceived, bidsWon, delivered, revenue);
    this.date = date;
  }

  public String getDate() {
    return date;
  }
}
