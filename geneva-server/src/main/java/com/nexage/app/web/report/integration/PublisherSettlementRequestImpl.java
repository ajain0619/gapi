package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.impl.finance.publishersettlement.PublisherSettlementRequest;

public class PublisherSettlementRequestImpl extends BaseReportRequest
    implements PublisherSettlementRequest {

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "NEXAGE_PUBLISHER_SETTLEMENT";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }
}
