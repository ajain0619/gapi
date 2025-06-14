package com.ssp.geneva.server.report.report.impl.seller.traffic;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Seller Traffic Report */
public enum TrafficReportDimension implements ReportDimension {
  site,
  position,
  adsource,
  tag,
  hour,
  day,
  week,
  month,
  headerBidding,
  s2sHbPartner;

  @Override
  public TrafficReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
