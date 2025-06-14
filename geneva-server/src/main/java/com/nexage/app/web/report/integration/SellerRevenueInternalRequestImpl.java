package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueInternalReportDimension;

public class SellerRevenueInternalRequestImpl extends SellerRevenueRequestImpl {

  private SellerRevenueInternalReportDimension dim;
  private Long seller;

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "SELLER_NET_REVENUE_INTERNAL";
        }
      };

  @Override
  public ReportDimension getDim() {
    return this.dim;
  }

  public void setDim(SellerRevenueInternalReportDimension dim) {
    this.dim = dim;
  }

  @Override
  public Long getSeller() {
    return seller;
  }

  public void setSeller(Long seller) {
    this.seller = seller;
  }

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }

  public void setReportMetadata(ReportMetadata reportMetadata) {
    this.reportMetadata = reportMetadata;
  }
}
