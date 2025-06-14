package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage.SubscriptionDataUsageReportDimension;
import com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage.SubscriptionDataUsageRequest;

public class SubscriptionDataUsageRequestImpl extends BaseReportRequest
    implements SubscriptionDataUsageRequest {
  private SubscriptionDataUsageReportDimension dim;
  private Long dataprovider;
  private Long buyer;

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "SUBSCRIPTION_DATA_USAGE";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }

  public ReportDimension getDim() {
    return dim;
  }

  public void setDim(SubscriptionDataUsageReportDimension dim) {
    this.dim = dim;
  }

  public Long getDataprovider() {
    return dataprovider;
  }

  public void setDataprovider(Long dataprovider) {
    this.dataprovider = dataprovider;
  }

  public Long getBuyer() {
    return buyer;
  }

  public void setBuyer(Long buyer) {
    this.buyer = buyer;
  }
}
