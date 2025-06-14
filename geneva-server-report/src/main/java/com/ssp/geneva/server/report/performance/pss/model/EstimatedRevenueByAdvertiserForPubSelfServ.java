package com.ssp.geneva.server.report.performance.pss.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EstimatedRevenueByAdvertiserForPubSelfServ implements Serializable {
  @JsonProperty("pid")
  private long publisherPid;

  @JsonProperty("directCampaign")
  private List<AdvertiserRevenue> directCampaign;

  public void setAdvertiserRevenue(long advertiserId, String advertiserName, Double revenue) {
    if (directCampaign == null) {
      directCampaign = new ArrayList<AdvertiserRevenue>();
    }
    directCampaign.add(new AdvertiserRevenue(advertiserId, advertiserName, revenue));
  }

  public void setPublisherPid(long publisherPid) {
    this.publisherPid = publisherPid;
  }

  public class AdvertiserRevenue {
    @JsonProperty("advertiserId")
    private long advertiserId;

    @JsonProperty("advertiserName")
    private String advertiserName;

    @JsonProperty("revenue")
    private Double revenue;

    AdvertiserRevenue() {}

    AdvertiserRevenue(long advertiserId, String advertiserName, Double revenue) {
      this.advertiserId = advertiserId;
      this.advertiserName = advertiserName;
      this.revenue = revenue;
    }
  }
}
