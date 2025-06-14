package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.impl.finance.adservingrevenue.AdServingRevenueRequest;

public class AdServingRevenueRequestImpl extends BaseReportRequest
    implements AdServingRevenueRequest {

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "NEXAGE_FINANCE_AD_SERVER";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }
}
