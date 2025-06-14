package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.impl.finance.financemetricsbyadsource.FinanceMetricsByAdSourceRequest;

public class FinanceMetricsByAdSourceRequestImpl extends BaseReportRequest
    implements FinanceMetricsByAdSourceRequest {

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "NEXAGE_FINANCE_METRICS_BY_AD_SOURCE";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }
}
