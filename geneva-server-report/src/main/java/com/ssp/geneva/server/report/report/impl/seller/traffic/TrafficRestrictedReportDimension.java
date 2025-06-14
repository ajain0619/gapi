package com.ssp.geneva.server.report.report.impl.seller.traffic;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Seller Traffic Restricted Report */
public enum TrafficRestrictedReportDimension implements ReportDimension {
  site,
  position,
  hour,
  day,
  week,
  month,
  headerBidding,
  s2sHbPartner;

  @Override
  public TrafficRestrictedReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
