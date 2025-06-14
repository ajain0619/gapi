package com.ssp.geneva.server.report.report.impl.seller.analyticsbysite;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Seller Analytics Restricted Report */
public enum AnalyticsRestrictedReportDimension implements ReportDimension {
  site,
  position,
  country,
  make,
  model,
  carrier,
  day,
  week,
  month;

  @Override
  public AnalyticsRestrictedReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
