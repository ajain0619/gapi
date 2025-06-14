package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.impl.internal.sitedistribution.RtbSiteDistributionRequest;

public class RtbSiteDistributionRequestImpl extends BaseReportRequest
    implements RtbSiteDistributionRequest {

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "RTB_SITE_DISTRIBUTION";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }
}
