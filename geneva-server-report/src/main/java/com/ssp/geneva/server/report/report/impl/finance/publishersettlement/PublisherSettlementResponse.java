package com.ssp.geneva.server.report.report.impl.finance.publishersettlement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;

@JsonInclude(Include.NON_NULL)
public class PublisherSettlementResponse implements ReportResponse {

  private Object publisherId;
  private Object publisherName;
  private Object costOfSales;
  private Object grossRevenue;
  private Object netRevenue;
  private Object totalRevenue;

  public final Object getPublisherId() {
    return publisherId;
  }

  public final void setPublisherId(Object publisherId) {
    this.publisherId = publisherId;
  }

  public final Object getPublisherName() {
    return publisherName;
  }

  public final void setPublisherName(Object publisherName) {
    this.publisherName = publisherName;
  }

  public final Object getCostOfSales() {
    return costOfSales;
  }

  public final void setCostOfSales(Object costOfSales) {
    this.costOfSales = costOfSales;
  }

  public final Object getGrossRevenue() {
    return grossRevenue;
  }

  public final void setGrossRevenue(Object grossRevenue) {
    this.grossRevenue = grossRevenue;
  }

  public final Object getNetRevenue() {
    return netRevenue;
  }

  public final void setNetRevenue(Object netRevenue) {
    this.netRevenue = netRevenue;
  }

  public final Object getTotalRevenue() {
    return totalRevenue;
  }

  public final void setTotalRevenue(Object totalRevenue) {
    this.totalRevenue = totalRevenue;
  }

  @Override
  public ResponseMetadata getResponseMetadata() {
    // TODO Auto-generated method stub
    return null;
  }
}
