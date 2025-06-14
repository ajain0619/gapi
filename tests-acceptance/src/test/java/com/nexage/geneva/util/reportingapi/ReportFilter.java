package com.nexage.geneva.util.reportingapi;

public enum ReportFilter {
  NOTHING("Nothing", ""),
  SITE("Site", "site"),
  SITE_ID("Site Id", "siteid"),
  SOURCE("Source", "source"),
  TAG("Tag", "tag"),
  TAG_ID("Tag Id", "tagid"),
  COUNTRY("Country", "country"),
  POSITION("Position", "position"),
  SOURCE_TYPE("Source Type", "sourceType"),
  MAKE("Make", "make"),
  MODEL("Model", "model"),
  OS("Os", "os"),
  OS_VERSION("Os Version", "osver"),
  CARRIER("Carrier", "carrier"),
  IMPRESSION_GROUP("Impression Group", "group"),
  BIDDER("Bidder", "bidder"),
  ADVERTISER_DOMAIN("Advertiser Domain", "adomain"),
  ADVERTISER("Advertiser", "advertiser"),
  CAMPAIGN_TYPE("Campaign Type", "campaigntype"),
  CAMPAIGN("Campaign", "campaign"),
  CREATIVE("Creative", "creative"),
  SEAT("Seat", "seat"),
  APP("App", "app"),
  HEADER_BIDDING("Header Bidding", "headerBidding");
  private String name, requestParameter;

  ReportFilter(String name, String requestParameter) {
    this.name = name;
    this.requestParameter = requestParameter;
  }

  public String getName() {
    return name;
  }

  public String getRequestParameter() {
    return requestParameter;
  }

  public static ReportFilter getReportFilter(String name) {
    ReportFilter result = null;

    for (ReportFilter reportFilter : values()) {
      if (reportFilter.getName().equals(name)) {
        result = reportFilter;
        break;
      }
    }

    return result;
  }
}
