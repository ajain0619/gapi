package com.ssp.geneva.server.report.report.impl.buyer.bidderspend;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.ReportRequest;

public interface BidderSpendRequest extends ReportRequest {

  ReportDimension getDim();

  Long getSite();

  Long getBidder();

  Long getBuyer();

  String getSeat();

  String getDeal();

  void setDeal(String deal);

  boolean isDealAsNullParam();

  void setDealAsNullParam(boolean hasDeal);
}
