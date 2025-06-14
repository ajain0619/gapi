package com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Subscription Data Usage Report. */
public enum SubscriptionDataUsageReportDimension implements ReportDimension {
  bidder;

  @Override
  public SubscriptionDataUsageReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
