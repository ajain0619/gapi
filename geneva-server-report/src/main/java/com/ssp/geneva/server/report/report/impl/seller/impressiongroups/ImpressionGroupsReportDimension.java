package com.ssp.geneva.server.report.report.impl.seller.impressiongroups;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Seller Impression Groups Report */
public enum ImpressionGroupsReportDimension implements ReportDimension {
  site,
  group,
  day,
  week,
  month;

  @Override
  public ImpressionGroupsReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
