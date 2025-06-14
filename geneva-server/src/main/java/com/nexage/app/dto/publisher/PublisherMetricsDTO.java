package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import com.nexage.admin.dw.util.DateUtil;
import com.nexage.admin.dw.util.ReportDefEnums.Interval;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class PublisherMetricsDTO {

  private final Interval interval;
  private List<MetricValue> requests;
  private List<MetricValue> served;
  private List<MetricValue> delivered;
  private List<MetricValue> revenue;
  private List<MetricValue> fillRate;
  private List<MetricValue> ctr;
  private List<MetricValue> rpm;
  private List<MetricValue> ecpm;
  private List<MetricValue> clicks;

  private Map<String, String> dateMap;

  public PublisherMetricsDTO(Interval interval) {
    this.interval = interval;
    requests = new ArrayList<>();
    served = new ArrayList<>();
    delivered = new ArrayList<>();
    clicks = new ArrayList<>();
    revenue = new ArrayList<>();
    fillRate = new ArrayList<>();
    ctr = new ArrayList<>();
    rpm = new ArrayList<>();
    ecpm = new ArrayList<>();

    dateMap = new HashMap<>();
  }

  public static class MetricValue {
    private String label;
    private BigDecimal value;

    MetricValue(String label, BigDecimal value) {
      this.label = label;
      this.value = value;
    }

    public String getLabel() {
      return label;
    }

    public BigDecimal getValue() {
      return value;
    }
  }

  private void addMetric(List<MetricValue> list, String date, BigDecimal value) {
    if (null != date) {
      if (null == value) {
        value = BigDecimal.ZERO;
      }
      list.add(new MetricValue(date, value));
    }
  }

  public void addRequest(String date, BigDecimal value) {
    addMetric(requests, date, value);
  }

  public void addServed(String date, BigDecimal value) {
    addMetric(served, date, value);
  }

  public void addDelivered(String date, BigDecimal value) {
    addMetric(delivered, date, value);
  }

  public void addClicks(String date, BigDecimal value) {
    addMetric(clicks, date, value);
  }

  public void addRevenue(String date, BigDecimal value) {
    addMetric(revenue, date, value);
  }

  public void addFillRate(String date, BigDecimal value) {
    addMetric(fillRate, date, value);
  }

  public void addCtr(String date, BigDecimal value) {
    addMetric(ctr, date, value);
  }

  public void addRpm(String date, BigDecimal value) {
    addMetric(rpm, date, value);
  }

  public void addEcpm(String date, BigDecimal value) {
    addMetric(ecpm, date, value);
  }

  public void addData(
      String date,
      BigDecimal request,
      BigDecimal served,
      BigDecimal delivered,
      BigDecimal clicks,
      BigDecimal revenue,
      BigDecimal fillRate,
      BigDecimal ctr,
      BigDecimal rpm,
      BigDecimal ecpm) {
    addRequest(date, request);
    addServed(date, served);
    addDelivered(date, delivered);
    addClicks(date, clicks);
    addRevenue(date, revenue);
    addFillRate(date, fillRate);
    addCtr(date, ctr);
    addRpm(date, rpm);
    addEcpm(date, ecpm);

    dateMap.put(date, date);
  }

  public void addNullData(String date) {
    addRequest(date, BigDecimal.ZERO);
    addServed(date, BigDecimal.ZERO);
    addDelivered(date, BigDecimal.ZERO);
    addClicks(date, BigDecimal.ZERO);
    addRevenue(date, BigDecimal.ZERO);
    addFillRate(date, BigDecimal.ZERO);
    addCtr(date, BigDecimal.ZERO);
    addRpm(date, BigDecimal.ZERO);
    addEcpm(date, BigDecimal.ZERO);
  }

  public boolean hasDate(String date) {
    return dateMap.containsKey(date);
  }

  @JsonView(PublisherMetricsViewDTO.Summary.class)
  public List<MetricValue> getRequests() {
    sortMetrics(requests);
    return requests;
  }

  @JsonView(PublisherMetricsViewDTO.Summary.class)
  public List<MetricValue> getServed() {
    sortMetrics(served);
    return served;
  }

  @JsonView(PublisherMetricsViewDTO.Summary.class)
  public List<MetricValue> getDelivered() {
    sortMetrics(delivered);
    return delivered;
  }

  @JsonView(PublisherMetricsViewDTO.SummaryWithClicks.class)
  public List<MetricValue> getClicks() {
    sortMetrics(clicks);
    return clicks;
  }

  @JsonView(PublisherMetricsViewDTO.Summary.class)
  public List<MetricValue> getRevenue() {
    sortMetrics(revenue);
    return revenue;
  }

  @JsonView(PublisherMetricsViewDTO.Summary.class)
  public List<MetricValue> getFillRate() {
    sortMetrics(fillRate);
    return fillRate;
  }

  @JsonView(PublisherMetricsViewDTO.Summary.class)
  public List<MetricValue> getCtr() {
    sortMetrics(ctr);
    return ctr;
  }

  @JsonView(PublisherMetricsViewDTO.Summary.class)
  public List<MetricValue> getRpm() {
    sortMetrics(rpm);
    return rpm;
  }

  @JsonView(PublisherMetricsViewDTO.Summary.class)
  public List<MetricValue> getEcpm() {
    sortMetrics(ecpm);
    return ecpm;
  }

  //  TODO: add to
  public static final SimpleDateFormat WEEK_IN_YEAR_FORMAT = new SimpleDateFormat("yyyy-ww");

  protected void sortMetrics(final List<MetricValue> metrics) {
    Comparator<MetricValue> comp =
        (metric1, metric2) -> {
          Calendar cal1 = Calendar.getInstance();
          Calendar cal2 = Calendar.getInstance();
          try {
            switch (interval) {
              case DAILY:
              default:
                cal1.setTime(DateUtil.parseFromDateFormat(metric1.getLabel()));
                cal2.setTime(DateUtil.parseFromDateFormat(metric2.getLabel()));
                break;
              case MONTHLY:
                cal1.setTime(DateUtil.parseFromMonthFormat(metric1.getLabel()));
                cal2.setTime(DateUtil.parseFromMonthFormat(metric2.getLabel()));
                break;

              case WEEKLY:
                cal1.setTime(WEEK_IN_YEAR_FORMAT.parse(metric1.getLabel()));
                cal2.setTime(WEEK_IN_YEAR_FORMAT.parse(metric2.getLabel()));
                break;
            }

          } catch (ParseException e) {
            return 0;
          }
          return cal1.compareTo(cal2);
        };
    metrics.sort(comp);
  }
}
