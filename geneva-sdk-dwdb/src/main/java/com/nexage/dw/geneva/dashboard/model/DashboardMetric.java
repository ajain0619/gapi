package com.nexage.dw.geneva.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonInclude(Include.NON_NULL)
public class DashboardMetric implements Serializable {

  private static final long serialVersionUID = -907351350575348420L;

  @JsonProperty("sellerMetrics")
  private SellerDashboardMetrics sellerDashboardMetrics;

  @JsonProperty("buyerMetrics")
  private BuyerDashboardMetrics buyerDashboardMetrics;

  public DashboardMetric(SellerDashboardMetrics sellerMetrics, BuyerDashboardMetrics buyerMetrics) {
    this.sellerDashboardMetrics = sellerMetrics;
    this.buyerDashboardMetrics = buyerMetrics;
  }

  public SellerDashboardMetrics getSellerDashboardMetrics() {
    return sellerDashboardMetrics;
  }

  public BuyerDashboardMetrics getBuyerDashboardMetrics() {
    return buyerDashboardMetrics;
  }

  @JsonInclude(Include.NON_NULL)
  public static final class SellerDashboardMetrics {
    private Metric requests;
    private Metric clicks;
    private Metric fillRate;
    private Metric ctr;
    private Metric revenue;
    private Metric eCpm;
    private Metric rpm;

    public SellerDashboardMetrics(BaseSellerMetrics summary, List<SellerDashboardDetail> details) {
      requests = new Metric(summary.getRequests());
      clicks = new Metric(summary.getClicks());
      fillRate = new Metric(summary.getFillRate());
      ctr = new Metric(summary.getCtr());
      revenue = new Metric(summary.getRevenue());
      eCpm = new Metric(summary.geteCpm());
      rpm = new Metric(summary.getRpm());
      for (SellerDashboardDetail metric : details) {
        requests.addMetricDetail(new MetricDetails(metric.getDate(), metric.getRequests()));
        clicks.addMetricDetail(new MetricDetails(metric.getDate(), metric.getClicks()));
        fillRate.addMetricDetail(new MetricDetails(metric.getDate(), metric.getFillRate()));
        ctr.addMetricDetail(new MetricDetails(metric.getDate(), metric.getCtr()));
        revenue.addMetricDetail(new MetricDetails(metric.getDate(), metric.getRevenue()));
        eCpm.addMetricDetail(new MetricDetails(metric.getDate(), metric.geteCpm()));
        rpm.addMetricDetail(new MetricDetails(metric.getDate(), metric.getRpm()));
      }
    }

    private SellerDashboardMetrics() {
      requests = new Metric(0);
      clicks = new Metric(0);
      fillRate = new Metric(0);
      ctr = new Metric(0);
      revenue = new Metric(0);
      eCpm = new Metric(0);
      rpm = new Metric(0);
    }

    public static SellerDashboardMetrics emptyMetric() {
      return new SellerDashboardMetrics();
    }

    public Metric getRequests() {
      return requests;
    }

    public Metric getClicks() {
      return clicks;
    }

    public Metric getFillRate() {
      return fillRate;
    }

    public Metric getCtr() {
      return ctr;
    }

    public Metric getRevenue() {
      return revenue;
    }

    public Metric geteCpm() {
      return eCpm;
    }

    public Metric getRpm() {
      return rpm;
    }
  }

  @JsonInclude(Include.NON_NULL)
  public static final class BuyerDashboardMetrics {
    private Metric requests;
    private Metric bidRate;
    private Metric impressions;
    private Metric winRate;
    private Metric spend;
    private Metric eCpm;

    public BuyerDashboardMetrics(BaseBuyerMetrics summary, List<BuyerDashboardDetail> details) {
      requests = new Metric(summary.getRequests());
      bidRate = new Metric(summary.getBidRate());
      impressions = new Metric(summary.getDelivered());
      winRate = new Metric(summary.getWinRate());
      spend = new Metric(summary.getRevenue());
      eCpm = new Metric(summary.geteCpm());
      for (BuyerDashboardDetail metric : details) {
        requests.addMetricDetail(new MetricDetails(metric.getDate(), metric.getRequests()));
        bidRate.addMetricDetail(new MetricDetails(metric.getDate(), metric.getBidRate()));
        impressions.addMetricDetail(new MetricDetails(metric.getDate(), metric.getDelivered()));
        winRate.addMetricDetail(new MetricDetails(metric.getDate(), metric.getWinRate()));
        spend.addMetricDetail(new MetricDetails(metric.getDate(), metric.getRevenue()));
        eCpm.addMetricDetail(new MetricDetails(metric.getDate(), metric.geteCpm()));
      }
    }

    private BuyerDashboardMetrics() {
      requests = new Metric(0);
      bidRate = new Metric(0);
      impressions = new Metric(0);
      winRate = new Metric(0);
      spend = new Metric(0);
      eCpm = new Metric(0);
    }

    public static BuyerDashboardMetrics emptyMetric() {
      return new BuyerDashboardMetrics();
    }

    public Metric getRequests() {
      return requests;
    }

    public Metric getBidRate() {
      return bidRate;
    }

    public Metric getImpressions() {
      return impressions;
    }

    public Metric getWinRate() {
      return winRate;
    }

    public Metric getSpend() {
      return spend;
    }

    public Metric geteCpm() {
      return eCpm;
    }
  }

  @JsonInclude(Include.NON_NULL)
  public static final class Metric {
    Integer trend;
    Object summary;
    Set<MetricDetails> details = new HashSet<>();

    public Metric(Object summary) {
      this.summary = summary;
    }

    public Integer getTrend() {
      return trend;
    }

    public void setTrend(Integer trend) {
      this.trend = trend;
    }

    public Object getSummary() {
      return summary;
    }

    public void setSummary(Object summary) {
      this.summary = summary;
    }

    public Set<MetricDetails> getDetails() {
      return details;
    }

    public void addMetricDetail(MetricDetails detail) {
      details.add(detail);
    }
  }

  @JsonInclude(Include.NON_NULL)
  public static final class MetricDetails {
    private final String date;
    private final Object value;

    public MetricDetails(String date, Object value) {
      this.date = date;
      this.value = value;
    }

    public String getDate() {
      return date;
    }

    public Object getValue() {
      return value;
    }
  }
}
