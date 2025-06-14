package com.nexage.app.dto;

// Maps the URL parameters to the report definition so that nexage-dw would work for Geneva
// seamlessly
public enum FilterParam {
  site("siteId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  position("position") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  adsource("adSourceId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  adSourceTypeId("adSourceTypeId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  tag("adTagId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  country("country") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  make("deviceMake") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  model("deviceModel") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  advertiser("advertiserId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  campaignType("campaignTypeId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  campaign("campaignId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  creative("creativeId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  bdrcreative("bdrcreativeId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  bidder("bidderId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  seat("seat") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  adomain("rtbAdvertiserDomain") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  company("publisherId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  hour("Hourly") {
    @Override
    public boolean isTimeDimension() {
      return true;
    }
  },
  day("Daily") {
    @Override
    public boolean isTimeDimension() {
      return true;
    }
  },
  week("Weekly") {
    @Override
    public boolean isTimeDimension() {
      return true;
    }
  },
  month("Monthly") {
    @Override
    public boolean isTimeDimension() {
      return true;
    }
  },
  buyer("buyerId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  authority("authorityId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  app("appId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  group("group") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  seatholder("seatHolderId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  seatadvertiser("seatAdvertiserId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  exchange("exchangeId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  insertionorder("insertionOrderId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  insertionorders("insertionOrders") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  lineitem("lineItemId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  targetgroup("targetgroupId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  insertionOrderType("insertionOrderTypeId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  exchangesite("exchangesite") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  deal("dealID") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  buyerBidder("buyerBidderId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  dataprovider("dataproviderId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  },
  seller("sellerId") {
    @Override
    public boolean isTimeDimension() {
      return false;
    }
  };

  private final String dwAlias;

  public abstract boolean isTimeDimension();

  FilterParam(String dwAlias) {
    this.dwAlias = dwAlias;
  }

  public String getDwAlias() {
    return dwAlias;
  }
}
