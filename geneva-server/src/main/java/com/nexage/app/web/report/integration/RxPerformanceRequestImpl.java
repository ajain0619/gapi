package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.impl.internal.rxperformance.RxPerformanceRequest;

/** RxPerformanceRequest implementation. */
public class RxPerformanceRequestImpl extends BaseReportRequest implements RxPerformanceRequest {

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "RX_PERFORMANCE";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }
}
