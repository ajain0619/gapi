package com.ssp.geneva.server.report.report.impl.finance.rtbbidderar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;

@JsonInclude(Include.NON_NULL)
public class RTBBidderARResponse implements ReportResponse {

  private Object bidder;
  private Object buyer;
  private Object bidRequests;
  private Object grossWins;
  private Object minTrafficCharge;
  private Object netAcquisitionCost;
  private Object totalCharge;

  @JsonIgnore private Object bidderId;

  public final Object getBidderId() {
    return bidderId;
  }

  public final void setBidderId(Object bidderId) {
    this.bidderId = bidderId;
  }

  public final Object getBidRequests() {
    return bidRequests;
  }

  public final void setBidRequests(Object bidRequests) {
    this.bidRequests = bidRequests;
  }

  public final Object getBidder() {
    return bidder;
  }

  public final void setBidder(Object bidder) {
    this.bidder = bidder;
  }

  public final Object getBuyer() {
    return buyer;
  }

  public final void setBuyer(Object buyer) {
    this.buyer = buyer;
  }

  public final Object getGrossWins() {
    return grossWins;
  }

  public final void setGrossWins(Object grossWins) {
    this.grossWins = grossWins;
  }

  public final Object getMinTrafficCharge() {
    return minTrafficCharge;
  }

  public final void setMinTrafficCharge(Object minTrafficCharge) {
    this.minTrafficCharge = minTrafficCharge;
  }

  public final Object getNetAcquisitionCost() {
    return netAcquisitionCost;
  }

  public final void setNetAcquisitionCost(Object netAcquisitionCost) {
    this.netAcquisitionCost = netAcquisitionCost;
  }

  public final Object getTotalCharge() {
    return totalCharge;
  }

  public final void setTotalCharge(Object totalCharge) {
    this.totalCharge = totalCharge;
  }

  @Override
  public ResponseMetadata getResponseMetadata() {
    // TODO Auto-generated method stub
    return null;
  }
}
