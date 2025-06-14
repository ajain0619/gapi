package com.ssp.geneva.server.report.report.util;

import java.util.HashMap;

public class ReportDefEnums {

  public enum DateRange {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    LAST_WEEK("Last Week"),
    LAST_MONTH("Last Month"),
    CUSTOM("Custom"),
    BY_MONTH("By Month");

    private String range;

    private static HashMap<String, DateRange> map =
        new HashMap<>() {

          private static final long serialVersionUID = 1L;

          {
            for (DateRange rs : DateRange.values()) {
              put(rs.getRange(), rs);
            }
          }
        };

    DateRange(String range) {
      this.range = range;
    }

    /** @return the range */
    public String getRange() {
      return range;
    }

    public static DateRange getRange(String range) {
      return map.get(range);
    }

    public static boolean isCustom(String d) {
      return d.equals(CUSTOM.getRange()) || d.equals(BY_MONTH.getRange());
    }
  }

  public enum Interval {
    HOURLY("Hourly"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly");

    private String interval;

    private static HashMap<String, Interval> map =
        new HashMap<>() {

          private static final long serialVersionUID = 1L;

          {
            for (Interval rs : Interval.values()) {
              put(rs.getInterval(), rs);
            }
          }
        };

    Interval(String interval) {
      this.interval = interval;
    }

    /** @return the range */
    public String getInterval() {
      return interval;
    }

    public static Interval getInterval(String interval) {
      return map.get(interval);
    }
  }

  public enum ApiInterval {
    HOURLY("hour"),
    DAILY("day"),
    WEEKLY("week"),
    MONTHLY("month");

    private String interval;

    private static HashMap<String, ApiInterval> map =
        new HashMap<String, ApiInterval>() {

          private static final long serialVersionUID = 1L;

          {
            for (ApiInterval rs : ApiInterval.values()) {
              put(rs.getInterval(), rs);
            }
          }
        };

    ApiInterval(String interval) {
      this.interval = interval;
    }

    /** @return the range */
    public String getInterval() {
      return interval;
    }

    public static ApiInterval getInterval(String interval) {
      return map.get(interval);
    }

    public Interval translateInterval() {
      //
      //  Athens uses a different enum for interval than reporting api
      //  Here we translate between them
      //
      Interval intervalToReturn = null;
      switch (this) {
        case HOURLY:
          intervalToReturn = Interval.HOURLY;
          break;
        case DAILY:
          intervalToReturn = Interval.DAILY;
          break;
        case WEEKLY:
          intervalToReturn = Interval.WEEKLY;
          break;
        case MONTHLY:
          intervalToReturn = Interval.MONTHLY;
          break;
      }
      return intervalToReturn;
    }
  }

  public enum DimOption {
    APP("appId"),
    SITE("site"),
    POSITION("position"),
    SOURCE("source"),
    TAG("tag"),
    COUNTRY("country"),
    MAKE("make"),
    MODEL("model"),
    CARRIER("carrier"),
    HOUR("hour"),
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    ADVERTISER("advertiser"),
    CAMPAIGN_TYPE("campaigntype"),
    CAMPAIGN("campaign"),
    CREATIVE("creative"),
    ADOMAIN("adomain"),
    SOURCETYPE("sourceType");

    private static HashMap<String, DimOption> map =
        new HashMap<String, DimOption>() {

          {
            for (DimOption ds : DimOption.values()) {
              put(ds.getDimOption(), ds);
            }
          }
        };

    private String dimOption;

    DimOption(String option) {
      this.dimOption = option;
    }

    /** @return the order */
    public String getDimOption() {
      return dimOption;
    }

    public static DimOption getDimOption(String option) {
      return map.get(option);
    }
  }

  public enum SortOrder {
    ASC("ASC"),
    DESC("DESC");

    private String order;

    SortOrder(String order) {
      this.order = order;
    }

    /** @return the order */
    public String getOrder() {
      return order;
    }
  }
}
