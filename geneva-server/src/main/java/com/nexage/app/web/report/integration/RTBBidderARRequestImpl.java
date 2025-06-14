package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.impl.finance.rtbbidderar.RTBBidderARRequest;

public class RTBBidderARRequestImpl extends BaseReportRequest implements RTBBidderARRequest {

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "RTB_BIDDER_AR";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }
}
