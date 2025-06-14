package com.nexage.admin.dw.reports.model;

import static java.util.Map.entry;

import com.nexage.admin.dw.util.ReportDefEnums.Interval;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.model.report.ReportType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryParameters {

  public static final String NEXAGE = "Nexage";
  public static final String BUYER = "Buyer";
  public static final String SELLER = "Seller";
  public static final String SEATHOLDER = "Seatholder";

  /**
   * Maps the dimension name in the report definition or models (used to send report data back to
   * client) to the fact table column name. Dim table mappings for the dimensions that requires id
   * -&gt; name conversion.
   */
  public enum DimKey {
    SITE(List.of("siteId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    ADSOURCE(List.of("adSourceId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    ADTAG(List.of("adTagId", "adTag")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    PUBLISHER(List.of("publisherId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    TIME(List.of("interval")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    BIDDER(List.of("bidderId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    AUCTION(List.of("auctionId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    POSITION(List.of("position")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    DEVICEMAKE(List.of("deviceMake")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    DEVICEMODEL(List.of("deviceModel")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    DEVICEOS(List.of("deviceOS", "OS")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    DEVICEOSV(List.of("deviceOSVersion", "OSVersion")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    ADVERTISER(List.of("advertiserId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    CAMPAIGN(List.of("campaignId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    CREATIVE(List.of("creativeId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    CAMPAIGNTYPE(List.of("campaignTypeId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    BUYER(List.of("buyerId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    APP(List.of("appId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    AUTHORITY(List.of("authorityId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    RTBADVERTISER(List.of("rtbAdvertiserDomain")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    SEAT(List.of("seat")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    COUNTRY(List.of("country")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    GROUP(List.of("group")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    SOURCETYPE(List.of("adSourceTypeId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    SEATHOLDER(List.of("seatHolderId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    INSERTIONORDER(List.of("insertionOrderId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    INSERTIONORDERTYPE(List.of("insertionOrderTypeId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    LINEITEM(List.of("lineItemId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    TARGETGROUP(List.of("targetgroupId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    EXCHANGE(List.of("exchangeId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    BDRCREATIVE(List.of("bdrcreativeId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    EXCHANGESITE(List.of("exchangesite")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    DEAL(List.of("dealID")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return false;
      }
    },
    BUYERBIDDER(List.of("buyerBidderId")) {
      @Override
      public boolean isBuyerBidderDrillDown() {
        return true;
      }
    };

    public static final Map<String, DimKey> defaultStringToEnum;

    static {
      Map<String, DimKey> tempStringToMap = new HashMap<>();
      for (DimKey dimKey : EnumSet.allOf(DimKey.class)) {
        for (String dimId : dimKey.dimIds) {
          tempStringToMap.put(dimId, dimKey);
        }
      }
      defaultStringToEnum = Collections.unmodifiableMap(tempStringToMap);
    }

    private final List<String> dimIds;

    DimKey(List<String> dimId) {
      this.dimIds = dimId;
    }

    public List<String> getDimIds() {
      return dimIds;
    }

    public String getDimId() {
      return dimIds.get(0);
    }

    public abstract boolean isBuyerBidderDrillDown();
  }

  /**
   * Maps the dimension name in the report definition or models (used to send report data back to
   * client) to the fact table column name. This will have an entry only for the dimensions whose
   * database names are different.
   */
  public static Map<String, String> mapDimPropToFactTableColumn =
      Map.ofEntries(
          entry("siteId", "site_id"),
          entry("adSourceId", "adnet_id"),
          entry("adTagId", "tag_id"),
          entry("adTag", "tag_id"),
          entry("publisherId", "company_id"),
          entry("interval", "start"),
          entry("bidderId", "bidder_id"),
          entry("auctionId", "auction_type"),
          entry("position", "zone"),
          entry("deviceMake", "device_make"),
          entry("make", "device_make"),
          entry("deviceModel", "device_model"),
          entry("model", "device_model"),
          entry("deviceOS", "device_os"),
          entry("OS", "device_os"),
          entry("deviceOSVersion", "device_osv"),
          entry("OSVersion", "device_osv"),
          entry("advertiserId", "advertiser_id"),
          entry("campaignId", "campaign_id"),
          entry("creativeId", "creative_id"),
          entry("campaignTypeId", "campaign_type"),
          entry("buyerId", "buyer_id"),
          entry("sellerId", "publisher_id"),
          entry("appId", "app_id"),
          entry(
              "authorityId",
              "authority"), // db name is inconsistent with other naming but that is the situation
          entry("rtbAdvertiserDomain", "advertiser"),
          entry("seat", "seat_id"),
          entry("adSourceTypeId", "source_type"),
          entry("seatHolderId", "seat_id"),
          entry("seatAdvertiserId", "advertiser_id"),
          entry("insertionOrderId", "io_id"),
          entry("exchangeId", "exchange_id"),
          entry("lineItemId", "lineitem_id"),
          entry("targetgroupId", "targetgroup_id"),
          entry("creativeId", "creative_id"),
          entry("bdrcreativeId", "creative_id"),
          entry("insertionOrderTypeId", "io_type"),
          entry("exchangesite", "exchange_site_name"),
          entry("dealID", "deal_id"),
          entry("dataProviderId", "data_provider_id"),
          entry(
              "buyerBidderId",
              null) // this is done intentionally as there is no fact column for buyerBidderId
          );

  /**
   * Dim table mappings for the dimensions that requires id -&gt; name conversion. The key is again
   * the dimension name in the report definition or models (they both should match)
   */
  public static Map<String, DimMappingData> dimMapping =
      Map.ofEntries(
          entry("siteId", new DimMappingData("dim_site", "id", "name")),
          entry("adSourceId", new DimMappingData("dim_adnet", "id", "name")),
          entry("adTagId", new DimMappingData("dim_tag", "id", "name")),
          entry("publisherId", new DimMappingData("dim_company", "id", "name")),
          entry("sellerId", new DimMappingData("dim_company", "id", "name")),
          entry("bidderId", new DimMappingData("dim_bidder", "id", "name")),
          entry("buyerId", new DimMappingData("dim_company", "id", "name")),
          entry("auctionId", new DimMappingData("dim_auction", "id", "name")),
          entry("customerId", new DimMappingData()),
          entry("advertiserId", new DimMappingData("dim_advertiser", "id", "name")),
          entry("campaignId", new DimMappingData("dim_campaign", "id", "name")),
          entry("creativeId", new DimMappingData("dim_creative", "id", "name")),
          entry("campaignTypeId", new DimMappingData("dim_campaign_type", "id", "name")),
          entry("authorityId", new DimMappingData("dim_authority", "id", "name")),
          entry("adSourceTypeId", new DimMappingData("dim_ad_source_type", "id", "name")),
          entry("seat", new DimMappingData("dim_company", "id", "name")),
          entry("seatHolderId", new DimMappingData("dim_company", "id", "name")),
          entry("dataProviderId", new DimMappingData("dim_data_provider", "id", "name")),
          entry("position", new DimMappingData("dim_position", "id", "name")),
          entry("dealID", new DimMappingData()),
          entry("buyerBidderId", new DimMappingData()));

  public static class DimMappingData {

    /** Dimension mapping table e.g., dim_site, dim_campaign_type, etc. */
    private final String mappingTable;

    /** Column that holds the unique mapping id e.g., id */
    private final String idColumn;

    /** Column that holds the mapping data e.g., name */
    private final String dataColumn;

    /**
     * Attribute in the report model that will have the mapped dim data. For eg., if the dimension
     * is a siteId, then it requires a mapping and "site" in SellerReport should be set to mapped
     * data. This is applicable only for dimensions that requires mapping.
     */
    private String dimDataInReportModel;

    private final List<String> otherColumns;

    public DimMappingData() {
      // fix nexage revenue report and remove this
      this(null, null, null, null);
    }

    public DimMappingData(String mappingTable, String idColumn, String dataColumn, String other) {
      this.mappingTable = mappingTable;
      this.idColumn = idColumn;
      this.dataColumn = dataColumn;
      if (other != null) {
        otherColumns = List.of(other.split(","));
      } else {
        otherColumns = new ArrayList<>();
      }
    }

    public DimMappingData(String mappingTable, String idColumn, String dataColumn) {
      this(mappingTable, idColumn, dataColumn, null);
    }

    public String getMappingTable() {
      return mappingTable;
    }

    public String getIdColumn() {
      return idColumn;
    }

    public String getDataColumn() {
      return dataColumn;
    }

    public String getDimDataInReportModel() {
      return dimDataInReportModel;
    }

    public void setDimDataInReportModel(String dimDataInReportModel) {
      this.dimDataInReportModel = dimDataInReportModel;
    }

    public List<String> getOtherColumns() {
      return otherColumns;
    }
  }

  private ReportType reportType;

  /** Company type of logged in user */
  private String companyType;

  /**
   * Dimension column name as in fact table. Set only if the report def and database names for a
   * dimension are different
   */
  private String dimColumnInFactTable;

  /**
   * Attribute in the report model that will have the dimension data as in fact table. For eg., if
   * the dimension is a siteId, then "siteId" in SellerReport would be set to the unique site id. if
   * the dim is country, then "country" in SellerReport would be set to country data.
   */
  private String dimIdInReportModel;

  /** Valid only if the dimension requires mapping else set to Null */
  private DimMappingData dimMappingData;

  /** Set only for interval based reports */
  private Interval interval;

  /**
   * Where clause attributes in a raw format expected by a query. e.g., key1 = value1 and key2 =
   * value2,..
   */
  private Map<String, Object> rawQueryConditionParamaters = new HashMap<String, Object>();

  /** List of dimension names involved in query condition as in report definition */
  private Set<String> queryConditionDimsAsInReportDef = new HashSet<String>();

  private String start;

  private String stop;

  private boolean includeOrderBy = false;

  private Integer rowLimit = null;

  private String loggedInUser;

  private DimKey dimKey;

  public String getLoggedInUser() {
    return loggedInUser;
  }

  public void setLoggedInUser(String loggedInUser) {
    this.loggedInUser = loggedInUser;
  }

  public QueryParameters(
      String start, String stop, ReportType type, Set<String> queryConditionDimsAsInReportDef) {
    this.start = start;
    this.stop = stop;
    this.reportType = type;
    this.queryConditionDimsAsInReportDef = queryConditionDimsAsInReportDef;
  }

  public Map<String, Object> getRawQueryConditionParamaters() {
    return rawQueryConditionParamaters;
  }

  public void setRawQueryConditionParamaters(Map<String, Object> queryConditionParamaters) {
    if (queryConditionParamaters != null) {
      this.rawQueryConditionParamaters = queryConditionParamaters;
    }
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getStop() {
    return stop;
  }

  public void setStop(String stop) {
    this.stop = stop;
  }

  public boolean isIncludeOrderBy() {
    return includeOrderBy;
  }

  public void setIncludeOrderBy(boolean includeOrderBy) {
    this.includeOrderBy = includeOrderBy;
  }

  public Integer getRowLimit() {
    return rowLimit;
  }

  public void setRowLimit(Integer rowLimit) {
    this.rowLimit = rowLimit;
  }

  public Interval getInterval() {
    return interval;
  }

  public void setInterval(Interval interval) {
    this.interval = interval;
  }

  public ReportType getReportType() {
    return reportType;
  }

  public void setReportType(ReportType reportType) {
    this.reportType = reportType;
  }

  public DimMappingData getDimMappingData() {
    return dimMappingData;
  }

  public boolean doesDimMappingDataExists() {
    return dimMappingData != null && dimMappingData.getMappingTable() != null;
  }

  public void setDimMappingData(DimMappingData dimMappingData) {
    this.dimMappingData = dimMappingData;
  }

  public String getDimColumnInFactTable() {
    return dimColumnInFactTable;
  }

  public void setDimColumnInFactTable(String dimColumnInFactTable) {
    this.dimColumnInFactTable = dimColumnInFactTable;
  }

  public String getDimIdInReportModel() {
    return dimIdInReportModel;
  }

  public void setDimKey() {
    dimKey = DimKey.defaultStringToEnum.get(dimIdInReportModel);
  }

  public DimKey getDimKey() {
    return dimKey;
  }

  public void setDimIdInReportModel(String dimIdInReportModel) {
    this.dimIdInReportModel = dimIdInReportModel;
    setDimKey();
  }

  public boolean isDrillDown() {
    return (getDimColumnInFactTable() != null);
  }

  public boolean isDrillDownByBuyerBidder() {
    return dimKey == DimKey.BUYERBIDDER;
  }

  public String getCompanyType() {
    return companyType;
  }

  public void setCompanyType(String companyType) {
    this.companyType = companyType;
  }

  public void setCompanyType(CompanyType type) {
    switch (type) {
      case NEXAGE:
        this.companyType = NEXAGE;
        break;

      case SELLER:
        this.companyType = SELLER;
        break;

      case BUYER:
        this.companyType = BUYER;
        break;

      case SEATHOLDER:
        this.companyType = SEATHOLDER;
        break;

      default:
        break;
    }
  }

  public Set<String> getQueryConditionDimsAsInReportDef() {
    return queryConditionDimsAsInReportDef;
  }

  public void setQueryConditionDimsAsInReportDef(Set<String> queryConditionDimsAsInReportDef) {
    this.queryConditionDimsAsInReportDef = queryConditionDimsAsInReportDef;
  }
}
