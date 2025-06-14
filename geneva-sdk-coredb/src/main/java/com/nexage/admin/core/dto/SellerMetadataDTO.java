package com.nexage.admin.core.dto;

import java.io.Serializable;

public class SellerMetadataDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private int numberOfUsers = 0;
  private int numberOfRtbTags = 0;
  private int numberOfMediationSites = 0;
  private int numberOfHbSites = 0;

  public SellerMetadataDTO() {}

  public SellerMetadataDTO(
      int numberOfUsers, int numberOfRtbTags, int numberOfMediationSites, int numberOfHbSites) {
    this.numberOfUsers = numberOfUsers;
    this.numberOfRtbTags = numberOfRtbTags;
    this.numberOfMediationSites = numberOfMediationSites;
    this.numberOfHbSites = numberOfHbSites;
  }

  public int getNumberOfRtbTags() {
    return numberOfRtbTags;
  }

  public void setNumberOfRtbTags(int numberOfRtbTags) {
    this.numberOfRtbTags = numberOfRtbTags;
  }

  public int getNumberOfMediationSites() {
    return numberOfMediationSites;
  }

  public void setNumberOfMediationSites(int numberOfMediationSites) {
    this.numberOfMediationSites = numberOfMediationSites;
  }

  public int getNumberOfHbSites() {
    return numberOfHbSites;
  }

  public void setNumberOfHbSites(int numberOfHbSites) {
    this.numberOfHbSites = numberOfHbSites;
  }

  public int getNumberOfUsers() {
    return numberOfUsers;
  }

  public void setNumberOfUsers(int numberOfUsers) {
    this.numberOfUsers = numberOfUsers;
  }
}
