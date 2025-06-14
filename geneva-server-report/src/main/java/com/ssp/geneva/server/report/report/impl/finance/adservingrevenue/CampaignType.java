package com.ssp.geneva.server.report.report.impl.finance.adservingrevenue;

public enum CampaignType {
  REMNANT_HOUSE("Remnant House"),
  PREMIUM_HOUSE_OVERAGE("Premium House Overage"),
  DIRECT_SOLD("Direct-Sold"),
  PREMIUM_HOUSE_WITHIN_ALLOWANCE("Premium House (within allowance)");

  private final String campaignType;

  CampaignType(String campaignType) {
    this.campaignType = campaignType;
  }

  public String getCampaignType() {
    return campaignType;
  }
}
