package com.nexage.geneva.util.reportingapi;

public enum ReportDrillDown {
  NOTHING("Nothing", ""),
  SITE("Site", "site"),
  SOURCE("Source", "source"),
  TAG("Tag", "tag"),
  COUNTRY("Country", "country"),
  POSITION("Position", "position"),
  SOURCE_TYPE("Source Type", "sourceType"),
  HOUR("Hour", "hour"),
  DAY("Day", "day"),
  WEEK("Week", "week"),
  MONTH("Month", "month"),
  MAKE("Make", "make"),
  MODEL("Model", "model"),
  OS("Os", "os"),
  OS_VERSION("Os Version", "osver"),
  CARRIER("Carrier", "carrier"),
  IMPRESSION_GROUP("Impression Group", "group"),
  BUYER("Buyer", "seat"),
  ADVERTISER("Advertiser", "advertiser"),
  ADOMAIN("Adomain", "adomain"),
  CAMPAIGN_TYPE("Campaign Type", "campaigntype"),
  CAMPAIGN("Campaign", "campaign"),
  CREATIVE("Creative", "creative"),
  SEAT("Seat", "seat"),
  APP("App", "app"),
  INVALID("Invalid", "invalid"),
  HEADER_BIDDING("Header Bidding", "headerBidding");
  private String name, requestParameter;

  ReportDrillDown(String drillDownName, String requestParameter) {
    this.name = drillDownName;
    this.requestParameter = requestParameter;
  }

  public String getName() {
    return name;
  }

  public String getRequestParameter() {
    return requestParameter;
  }

  public String getDrillDownUrlPart() {
    return "dim";
  }

  public static ReportDrillDown getReportDrillDown(String name) {
    ReportDrillDown result = null;

    for (ReportDrillDown reportDrillDown : values()) {
      if (reportDrillDown.getName().equals(name)) {
        result = reportDrillDown;
        break;
      }
    }

    return result;
  }
}
