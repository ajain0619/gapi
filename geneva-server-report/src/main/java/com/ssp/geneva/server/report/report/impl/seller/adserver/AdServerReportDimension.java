package com.ssp.geneva.server.report.report.impl.seller.adserver;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Seller Ad Server Report */
public enum AdServerReportDimension implements ReportDimension {
  site,
  position,
  tag,
  advertiser,
  campaignType,
  campaign,
  creative,
  hour,
  day,
  week,
  month;

  @Override
  public AdServerReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
