package com.ssp.geneva.common.model.report;

import java.util.HashMap;
import java.util.Map;

public enum ReportType {
  RTB_BIDDER_AR(FacadeMarker.NexageFinanceReportFacacde),
  RX_PERFORMANCE(FacadeMarker.ExchangeReportFacade),
  BIDDER_ACTIVITY(FacadeMarker.ExchangeReportFacade),
  BIDDER_DISTRIBUTION(FacadeMarker.ExchangeReportFacade),
  RTB_SITE_DISTRIBUTION(FacadeMarker.BuyerReportFacade, "rtbsitedistribution"),
  MEDIATION_RTB_REVENUE(FacadeMarker.NexageFinanceReportFacacde),
  NEXAGE_FINANCE_METRICS_BY_SITE(FacadeMarker.NexageFinanceReportFacacde),
  NEXAGE_FINANCE_METRICS_BY_SITE_MONETIZED(FacadeMarker.NexageFinanceReportFacacde),
  NEXAGE_FINANCE_METRICS_BY_ADSOURCE(FacadeMarker.NexageFinanceReportFacacde),
  NEXAGE_PUBLISHER_SETTLEMENT(FacadeMarker.NexageFinanceReportFacacde),
  ANALYTICS_BY_SITE(false, true, FacadeMarker.SellerReportFacade, "selleranalytics"),
  TRAFFIC_BY_SITE(false, true, FacadeMarker.SellerReportFacade, "sellertraffic"),
  REVENUE_BY_SITE(false, true, FacadeMarker.SellerReportFacade, "sellerrevenue"),
  IMPRESSION_GROUP(true, true, FacadeMarker.SellerReportFacade, "sellerimpressiongroups"),
  IMPRESSION_GROUP_NET(true, true, FacadeMarker.SellerReportFacade, "sellerimpressionnetgroups"),
  IMPRESSION_GROUP_NET_INTERNAL(
      true, true, FacadeMarker.SellerReportFacade, "sellerimpressionnetgroupsinternal"),
  ANALYTICS_BY_SITE_RESTRICTED_DD(true, false, FacadeMarker.SellerReportFacade, null),
  TRAFFIC_BY_SITE_RESTRICTED_DD(true, false, FacadeMarker.SellerReportFacade, null),
  REVENUE_BY_SITE_RESTRICTED_DD(true, false, FacadeMarker.SellerReportFacade, null),
  AD_SERVER(true, true, FacadeMarker.SellerReportFacade, "adserveperf"),
  NEXAGE_FINANCE_AD_SERVER(FacadeMarker.NexageFinanceReportFacacde),
  DATAPROVIDER_USAGE(FacadeMarker.ExchangeReportFacade),
  SUBSCRIPTION_DATA_USAGE(
      FacadeMarker.BuyerReportFacade,
      "rtbsubscriptiondatausage",
      "5FCD2CDEFC3811E18A26DA416188709B"),
  CPI_ANALYTICS(FacadeMarker.BuyerReportFacade),
  CPI_CONVERSION(
      FacadeMarker.BuyerReportFacade, "buyercpiconversion", "8b256a47013d2d15b2bd11b2c41432d4"),
  RTB_REVENUE_PERFORMANCE(true, true, FacadeMarker.SellerReportFacade, null),
  RTB_NET_REVENUE_PERFORMANCE(true, true, FacadeMarker.SellerReportFacade, null),
  RTB_NET_REVENUE_PERFORMANCE_INTERNAL(true, true, FacadeMarker.SellerReportFacade, null),
  BIDDER_PERFORMANCE_SEAT(
      FacadeMarker.BuyerReportFacade,
      "rtbbidderperformanceseat",
      "35f8b906004b11e38d57283bafba97c6"),
  BIDDER_SPEND(FacadeMarker.BuyerReportFacade, "bidderspend", "a3d17694f4f711e4b0e5d4bed9f6c105"),
  SEAT_PERFORMANCE(FacadeMarker.SeatHolderReportFacade),
  SEAT_PERFORMANCE_SEATHOLDER(FacadeMarker.SeatHolderReportFacade),
  SEAT_CREATIVE_PERFORMANCE(FacadeMarker.SeatHolderReportFacade),
  SELLER_NET_REVENUE(false, true, FacadeMarker.SellerReportFacade, "sellernetrevenue"),
  SELLER_NET_REVENUE_RESTRICTED_DD(true, false, FacadeMarker.SellerReportFacade, null),
  SELLER_NET_REVENUE_INTERNAL(false, true, FacadeMarker.SellerReportFacade, null);

  private final boolean availableToRestrictedSellers;
  private final boolean availableToNonRestrictedSellers;
  private final FacadeMarker facadeMarker;
  private final String urlKey;
  private String reportDefId;

  ReportType(FacadeMarker facadeMarker) {
    this(true, true, facadeMarker, null);
  }

  ReportType(FacadeMarker facadeMarker, String urlKey) {
    this(true, true, facadeMarker, urlKey);
  }

  ReportType(FacadeMarker facadeMarker, String urlKey, String reportDefId) {
    this(true, true, facadeMarker, urlKey);
    this.reportDefId = reportDefId;
  }

  ReportType(
      boolean availableToRestrictedSellers,
      boolean availableToNonRestrictedSellers,
      FacadeMarker marker,
      String urlKey) {
    this.availableToRestrictedSellers = availableToRestrictedSellers;
    this.availableToNonRestrictedSellers = availableToNonRestrictedSellers;
    facadeMarker = marker;
    this.urlKey = urlKey;
  }

  public boolean isAvailableToRestrictedSellers() {
    return availableToRestrictedSellers;
  }

  public boolean isAvailableToNonRestrictedSellers() {
    return availableToNonRestrictedSellers;
  }

  public String reportDefId() {
    return reportDefId;
  }

  public boolean belongsToThisFacade(FacadeMarker marker) {
    return facadeMarker == marker;
  }

  public static ReportType getReportTypeFromURL(String url) {
    for (ReportType rt : ReportType.values()) {
      if (rt.urlKey != null && url.contains(rt.urlKey)) {
        return rt;
      }
    }
    return null;
  }

  public static ReportType getReportTypeFromId(String reportDefId) {
    return reportDefTypeMap.get(reportDefId);
  }

  private static Map<String, ReportType> reportDefTypeMap =
      new HashMap<>() {
        {
          put(ReportType.BIDDER_SPEND.reportDefId, ReportType.BIDDER_SPEND);
          put(ReportType.SUBSCRIPTION_DATA_USAGE.reportDefId, ReportType.SUBSCRIPTION_DATA_USAGE);
          put(ReportType.CPI_CONVERSION.reportDefId, ReportType.CPI_CONVERSION);
        }
      };
}
