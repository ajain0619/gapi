package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.impl.finance.financemetricsbysite.FinanceMetricsBySiteRequest;

public class FinanceMetricsBySiteRequestImpl extends BaseReportRequest
    implements FinanceMetricsBySiteRequest {

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "NEXAGE_FINANCE_METRICS_BY_SITE";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }
}
