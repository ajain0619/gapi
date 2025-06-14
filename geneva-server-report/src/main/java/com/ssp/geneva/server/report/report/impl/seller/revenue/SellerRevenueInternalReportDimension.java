package com.ssp.geneva.server.report.report.impl.seller.revenue;

import com.ssp.geneva.server.report.report.ReportDimension;

/** Dimensions for Seller Revenue Internal Report */
public enum SellerRevenueInternalReportDimension implements ReportDimension {
  seller,
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
  public SellerRevenueInternalReportDimension getDimension() {
    return this;
  }

  @Override
  public String getName() {
    return this.name();
  }
}
