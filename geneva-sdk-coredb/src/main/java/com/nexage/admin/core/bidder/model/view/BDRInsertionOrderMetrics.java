package com.nexage.admin.core.bidder.model.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BDRInsertionOrderMetrics {

  private final Long pid;
  private final Type type;
  private final Long parentPid;

  private BDRInsertionOrderMetrics(Builder builder) {
    this.pid = builder.pid;
    this.type = builder.type;
    this.parentPid = builder.parentPid;
  }

  public Long getPid() {
    return pid;
  }

  public Type getType() {
    return type;
  }

  public Long getParentPid() {
    return parentPid;
  }

  // this is the metrics data to be returned
  private List<MetricData> metrics = new ArrayList<MetricData>();

  // this is an internal only map  - no getter
  private Map<String, MetricData> metricsMap = new HashMap<>();

  public void addData(
      String timeString,
      Date date,
      BigDecimal spend,
      Long impressions,
      Long clicks,
      Long conversions) {
    metricsMap.put(
        timeString, new MetricData(timeString, date, spend, impressions, clicks, conversions));
  }

  public void addNullData(String label, Date time) {
    addData(label, time, BigDecimal.ZERO, 0L, 0L, 0L);
  }

  // get the sorted return list of metric data
  public List<MetricData> getMetrics() {
    metrics = new ArrayList<MetricData>(metricsMap.values());
    sortMetrics(metrics);
    return metrics;
  }

  public MetricData findData(String time) {
    if (metricsMap.containsKey(time)) {
      return metricsMap.get(time);
    }
    return null;
  }

  protected void sortMetrics(final List<MetricData> metrics) {
    Comparator<MetricData> comp =
        new Comparator<MetricData>() {
          @Override
          public int compare(MetricData metric1, MetricData metric2) {
            return metric1.getDate().compareTo(metric2.getDate());
          }
        };
    Collections.sort(metrics, comp);
  }

  public enum Type {
    InsertionOrder,
    LineItem,
    TargetGroup,
    Creative;
  }

  public static final class Builder {
    private Long pid;
    private Type type;
    private Long parentPid;

    public BDRInsertionOrderMetrics build() {
      return new BDRInsertionOrderMetrics(this);
    }

    public Builder setPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder setType(Type type) {
      this.type = type;
      return this;
    }

    public Builder setParentPid(Long parentPid) {
      this.parentPid = parentPid;
      return this;
    }
  }

  public static class MetricData {
    String label;
    private Date date;
    private BigDecimal spend;
    private Long impressions;
    private Long clicks;
    private Long conversions;

    private BigDecimal ctr = BigDecimal.ZERO;
    private BigDecimal ecpm = BigDecimal.ZERO;
    private BigDecimal ecpi = BigDecimal.ZERO;
    private BigDecimal ecpc = BigDecimal.ZERO;
    private final BigDecimal thousand = BigDecimal.valueOf(1000L);

    public MetricData(
        String label,
        Date date,
        BigDecimal spend,
        Long impressions,
        Long clicks,
        Long conversions) {
      this.label = label;
      this.date = date;
      this.spend = spend;
      this.impressions = impressions;
      this.clicks = clicks;
      this.conversions = conversions;

      int scale = 6;
      if (0 < impressions) {
        BigDecimal imps = new BigDecimal(impressions);
        ctr = BigDecimal.valueOf(clicks).divide(imps, scale, RoundingMode.HALF_UP);
        ecpm = spend.divide(imps, scale, RoundingMode.HALF_UP).multiply(thousand);
      }

      if (0 < conversions) {
        ecpi =
            spend
                .divide(BigDecimal.valueOf(conversions), scale, RoundingMode.HALF_UP)
                .multiply(thousand);
      }

      if (0 < clicks) {
        ecpc =
            spend
                .divide(BigDecimal.valueOf(clicks), scale, RoundingMode.HALF_UP)
                .multiply(thousand);
      }
    }

    public String getLabel() {
      return label;
    }

    public Date getDate() {
      return date;
    }

    public BigDecimal getSpend() {
      return spend;
    }

    public Long getImpressions() {
      return impressions;
    }

    public Long getClicks() {
      return clicks;
    }

    public Long getConversions() {
      return conversions;
    }

    public BigDecimal getCtr() {
      return ctr;
    }

    public BigDecimal getEcpm() {
      return ecpm;
    }

    public BigDecimal getEcpi() {
      return ecpi;
    }

    public BigDecimal getEcpc() {
      return ecpc;
    }
  }
}
