package com.ssp.geneva.server.report.report.impl.seller.revenue;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Seller Revenue Restricted Report */
public enum SellerRevenueRestrictedReportDimension implements ReportDimension {
  site,
  country,
  position,
  adSourceTypeId,
  day,
  week,
  month;

  @Override
  public SellerRevenueRestrictedReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
