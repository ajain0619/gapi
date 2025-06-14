package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueReportDimension;

public class SellerRevenueRequestImpl extends SellerRevenueRestrictedRequestImpl {

  private SellerRevenueReportDimension dim;
  private Long adsource;
  private Long tag;

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "SELLER_NET_REVENUE";
        }
      };

  @Override
  public ReportDimension getDim() {
    return this.dim;
  }

  public void setDim(SellerRevenueReportDimension dim) {
    this.dim = dim;
  }

  @Override
  public Long getAdsource() {
    return adsource;
  }

  public void setAdsource(Long adsource) {
    this.adsource = adsource;
  }

  @Override
  public Long getTag() {
    return tag;
  }

  public void setTag(Long tag) {
    this.tag = tag;
  }

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }

  public void setReportMetadata(ReportMetadata reportMetadata) {
    this.reportMetadata = reportMetadata;
  }
}
