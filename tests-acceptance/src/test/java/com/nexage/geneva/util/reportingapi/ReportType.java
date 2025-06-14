package com.nexage.geneva.util.reportingapi;

public enum ReportType {
  SELLER_REVENUE("Seller Revenue", "sellerrevenue"),
  SELLER_NET_REVENUE_SUMMARY("Seller Net Revenue Summary", "sellernetrevenuesummary"),
  SELLER_TRAFFIC("Seller Traffic", "sellertraffic"),
  SELLER_ANALYTICS("Seller Analytics", "selleranalytics"),
  SELLER_IMPRESSION_GROUPS("Seller Impression Groups", "sellerimpressiongroups"),
  SELLER_RTB_REVENUE_PERFORMANCE("Seller RTB Revenue Performance", "sellerrtbrevenue"),
  SELLER_AD_SERVER("Seller Ad Server", "adserveperf"),
  BUYER_BIDDER_PERFORMANCE("Buyer Bidder Performance", "bidderseatperformance"),
  BUYER_CPI_CONVERSION("Buyer CPI Conversions", "cpiconversions");
  private String name, requestParam;

  ReportType(String name, String requestParam) {
    this.name = name;
    this.requestParam = requestParam;
  }

  public String getName() {
    return name;
  }

  public String getRequestParam() {
    return requestParam;
  }

  public static ReportType getReportType(String name) {
    ReportType result = null;
    for (ReportType reportsApi : values()) {
      if (reportsApi.getName().equals(name)) {
        result = reportsApi;
        break;
      }
    }
    return result;
  }
}
