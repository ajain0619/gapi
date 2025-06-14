package com.ssp.geneva.server.report.report.impl.buyer.bidderspend;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Bidder Spend Report */
public enum BidderSpendReportDimension implements ReportDimension {
  site,
  buyer,
  bidder,
  seat,
  deal,
  hour,
  day,
  week,
  month;

  @Override
  public BidderSpendReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
