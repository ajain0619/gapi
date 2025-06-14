package com.ssp.geneva.server.report.performance.pss.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EstimatedRevenueByAdNetworksForPubSelfServ implements Serializable {
  @JsonProperty("pid")
  private long publisherPid;

  @JsonProperty("mediation")
  private List<BuyerRevenue> mediation;

  public void setBuyerRevenue(long buyerId, String buyerName, Double revenue) {
    if (mediation == null) {
      mediation = new ArrayList<BuyerRevenue>();
    }
    mediation.add(new BuyerRevenue(buyerId, buyerName, revenue));
  }

  public void setPublisherPid(long publisherPid) {
    this.publisherPid = publisherPid;
  }

  public class BuyerRevenue {
    @JsonProperty("buyerId")
    private long buyerId;

    @JsonProperty("buyerName")
    private String buyerName;

    @JsonProperty("revenue")
    private Double revenue;

    BuyerRevenue() {}

    BuyerRevenue(long buyerId, String buyerName, Double revenue) {
      this.buyerId = buyerId;
      this.buyerName = buyerName;
      this.revenue = revenue;
    }
  }
}
