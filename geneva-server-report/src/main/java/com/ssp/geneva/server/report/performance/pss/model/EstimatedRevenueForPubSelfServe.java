package com.ssp.geneva.server.report.performance.pss.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class EstimatedRevenueForPubSelfServe implements Serializable {
  @JsonProperty("pid")
  private long publisherPid;

  @JsonProperty("nexage")
  private double nexageRev;

  @JsonProperty("millenialMedia")
  private double millenialMediaRev;

  @JsonProperty("mediation")
  private double mediationRev;

  @JsonProperty("directCampaigns")
  private double directCampaignsRev;

  public EstimatedRevenueForPubSelfServe() {}

  public EstimatedRevenueForPubSelfServe(
      long publisherPid,
      double nexageRev,
      double millenialMediaRev,
      double mediationRev,
      double directCampaignsRev) {
    this.publisherPid = publisherPid;
    this.nexageRev = nexageRev;
    this.millenialMediaRev = millenialMediaRev;
    this.mediationRev = mediationRev;
    this.directCampaignsRev = directCampaignsRev;
  }
}
