package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.traffic.TrafficReportDimension;

/** POJO for Traffic report request. */
public class TrafficRequestImpl extends TrafficRestrictedRequestImpl {

  private TrafficReportDimension dim;
  private Long adsource;
  private Long tag;

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "TRAFFIC_BY_SITE";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }

  @Override
  public ReportDimension getDim() {
    return this.dim;
  }

  public void setDim(TrafficReportDimension dim) {
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
}
