package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.impl.buyer.bidderspend.BidderSpendReportDimension;
import com.ssp.geneva.server.report.report.impl.buyer.bidderspend.BidderSpendRequest;

public class BidderSpendRequestImpl extends BaseReportRequest implements BidderSpendRequest {

  private BidderSpendReportDimension dim;
  private Long site;
  private Long bidder;
  private Long buyer;
  private String seat;
  private String deal;
  private boolean dealAsNullParam;

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "BIDDER_SPEND";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }

  @Override
  public ReportDimension getDim() {
    return dim;
  }

  public void setDim(BidderSpendReportDimension dim) {
    this.dim = dim;
  }

  @Override
  public Long getSite() {
    return site;
  }

  public void setSite(Long site) {
    this.site = site;
  }

  @Override
  public Long getBidder() {
    return bidder;
  }

  public void setBidder(Long bidder) {
    this.bidder = bidder;
  }

  @Override
  public Long getBuyer() {
    return buyer;
  }

  public void setBuyer(Long buyer) {
    this.buyer = buyer;
  }

  @Override
  public String getSeat() {
    return seat;
  }

  public void setSeat(String seat) {
    this.seat = seat;
  }

  @Override
  public String getDeal() {
    return deal;
  }

  @Override
  public void setDeal(String deal) {
    this.deal = deal;
  }

  @Override
  public boolean isDealAsNullParam() {
    return dealAsNullParam;
  }

  @Override
  public void setDealAsNullParam(boolean dealAsNullParam) {
    this.dealAsNullParam = dealAsNullParam;
  }
}
