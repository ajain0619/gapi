package com.nexage.geneva.util.geneva;

public enum CampaignStatus {
  RUN("run", "ACTIVE"),
  CREATE_ACTIVE("create valid", "INACTIVE"),
  CREATE_INCOMPLETE("create incomplete", "INACTIVE"),
  PAUSE("pause", "PAUSED"),
  DEPLOY("deploy", "ACTIVE"),
  COMPLETE("complete", "COMPLETED"),
  ARCHIVED("archive", "COMPLETED");
  private String action, status;

  CampaignStatus(String action, String status) {
    this.action = action;
    this.status = status;
  }

  public String getAction() {
    return this.action;
  }

  public String getStatus() {
    return this.status;
  }

  public static CampaignStatus getCampaignStatus(String action) {
    CampaignStatus result = null;

    for (CampaignStatus campaignStatus : values()) {
      if (campaignStatus.getAction().equals(action)) {
        result = campaignStatus;
      }
    }
    return result;
  }
}
