package com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.ReportRequest;

public interface SubscriptionDataUsageRequest extends ReportRequest {

  ReportDimension getDim();

  Long getDataprovider();

  void setDataprovider(Long dataprovider);

  Long getBuyer();

  void setBuyer(Long buyer);
}
