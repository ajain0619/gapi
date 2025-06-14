package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.impl.finance.mediationandrtbrevenue.MediationAndRTBRevenueRequest;

public class MediationAndRTBRevenueRequestImpl extends BaseReportRequest
    implements MediationAndRTBRevenueRequest {

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "MEDIATION_RTB_REVENUE";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }
}
