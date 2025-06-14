package com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance;

import com.ssp.geneva.server.report.report.ReportDimension;

public enum RtbRevenuePerformanceDimension implements ReportDimension {
  adomain,
  seat,
  deal,
  bidder,
  buyer,
  site,
  position,
  tag,
  country,
  hour,
  day,
  week,
  month,
  s2sHbPartner;

  @Override
  public RtbRevenuePerformanceDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
