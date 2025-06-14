package com.ssp.geneva.server.report.report.impl.seller.revenue;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Seller Revenue Report */
public enum SellerRevenueReportDimension implements ReportDimension {
  site,
  adsource,
  tag,
  country,
  position,
  adSourceTypeId,
  day,
  week,
  month,
  headerBidding;

  @Override
  public SellerRevenueReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
