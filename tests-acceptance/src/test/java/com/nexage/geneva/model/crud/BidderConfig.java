package com.nexage.geneva.model.crud;

/** Created by rasangasamarasinghe on 22/04/2016. */
public class BidderConfig {
  private String id;
  private int pid;
  private int bidrequestUseridPreference;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getPid() {
    return pid;
  }

  public void setPid(int pid) {
    this.pid = pid;
  }

  public int getBidrequestUseridPreference() {
    return bidrequestUseridPreference;
  }

  public void setBidrequestUseridPreference(int bidrequestUseridPreference) {
    this.bidrequestUseridPreference = bidrequestUseridPreference;
  }
}
