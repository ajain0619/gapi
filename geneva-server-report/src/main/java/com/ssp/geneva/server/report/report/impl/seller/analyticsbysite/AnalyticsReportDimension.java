package com.ssp.geneva.server.report.report.impl.seller.analyticsbysite;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Seller Analytics Report */
public enum AnalyticsReportDimension implements ReportDimension {
  site,
  position,
  adsource,
  tag,
  country,
  make,
  model,
  carrier,
  day,
  week,
  month;

  @Override
  public AnalyticsReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
