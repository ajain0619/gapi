package com.nexage.app.dto.pub.self.serve;

import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;

public class PubSelfServeSummaryMetrics {

  private PubSelfServeMetricSummaryOverTimeDTO requests;
  private PubSelfServeMetricSummaryOverTimeDTO served;
  private PubSelfServeMetricSummaryOverTimeDTO delivered;
  private PubSelfServeMetricSummaryOverTimeDTO clicks;
  private PubSelfServeMetricSummaryOverTimeDTO revenue;
  private PubSelfServeMetricSummaryOverTimeDTO fillRate;
  private PubSelfServeMetricSummaryOverTimeDTO ctr;
  private PubSelfServeMetricSummaryOverTimeDTO rpm;
  private PubSelfServeMetricSummaryOverTimeDTO ecpm;

  public PubSelfServeSummaryMetrics() {
    this.requests = new PubSelfServeMetricSummaryOverTimeDTO();
    this.served = new PubSelfServeMetricSummaryOverTimeDTO();
    this.delivered = new PubSelfServeMetricSummaryOverTimeDTO();
    this.clicks = new PubSelfServeMetricSummaryOverTimeDTO();
    this.revenue = new PubSelfServeMetricSummaryOverTimeDTO();
    this.fillRate = new PubSelfServeMetricSummaryOverTimeDTO();
    this.ctr = new PubSelfServeMetricSummaryOverTimeDTO();
    this.rpm = new PubSelfServeMetricSummaryOverTimeDTO();
    this.ecpm = new PubSelfServeMetricSummaryOverTimeDTO();
  }

  public void aggregate(PubSelfServeMetrics m) {
    requests.aggregate(m.getCurrentInboundReqs(), m.getPrevInboundReqs());
    served.aggregate(m.getCurrentServed(), m.getPrevServed());
    delivered.aggregate(m.getCurrentDelivered(), m.getPrevDelivered());
    clicks.aggregate(m.getCurrentClicks(), m.getPrevClicks());
    revenue.aggregate(m.getCurrentRevenue(), m.getPrevRevenue());
  }

  public PubSelfServeMetricSummaryOverTimeDTO getRequests() {
    return requests;
  }

  public void setRequests(PubSelfServeMetricSummaryOverTimeDTO requests) {
    this.requests = requests;
  }

  public PubSelfServeMetricSummaryOverTimeDTO getServed() {
    return served;
  }

  public void setServed(PubSelfServeMetricSummaryOverTimeDTO served) {
    this.served = served;
  }

  public PubSelfServeMetricSummaryOverTimeDTO getDelivered() {
    return delivered;
  }

  public void setDelivered(PubSelfServeMetricSummaryOverTimeDTO delivered) {
    this.delivered = delivered;
  }

  public PubSelfServeMetricSummaryOverTimeDTO getClicks() {
    return clicks;
  }

  public void setClicks(PubSelfServeMetricSummaryOverTimeDTO clicks) {
    this.clicks = clicks;
  }

  public PubSelfServeMetricSummaryOverTimeDTO getRevenue() {
    return revenue;
  }

  public void setRevenue(PubSelfServeMetricSummaryOverTimeDTO revenue) {
    this.revenue = revenue;
  }

  public PubSelfServeMetricSummaryOverTimeDTO getFillRate() {
    if (requests.current > 0) fillRate.current = (served.current / (double) requests.current) * 100;
    if (requests.past > 0) fillRate.past = (served.past / (double) requests.past) * 100;
    if (fillRate.current > 100) fillRate.current = 100;
    if (fillRate.past > 100) fillRate.past = 100;
    return fillRate;
  }

  public PubSelfServeMetricSummaryOverTimeDTO getCtr() {
    if (delivered.current > 0) ctr.current = (clicks.current / (double) delivered.current) * 100;
    if (delivered.past > 0) ctr.past = (clicks.past / (double) delivered.past) * 100;
    if (ctr.current > 100) ctr.current = 100;
    if (ctr.past > 100) ctr.past = 100;
    return ctr;
  }

  public PubSelfServeMetricSummaryOverTimeDTO getRpm() {
    if (requests.current > 0) rpm.current = (revenue.current / (double) requests.current) * 1000;
    if (requests.past > 0) rpm.past = (revenue.past / (double) requests.past) * 1000;
    return rpm;
  }

  public PubSelfServeMetricSummaryOverTimeDTO getEcpm() {
    if (delivered.current > 0) ecpm.current = (revenue.current / (double) delivered.current) * 1000;
    if (delivered.past > 0) ecpm.past = (revenue.past / (double) delivered.past) * 1000;
    return ecpm;
  }
}
