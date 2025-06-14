package com.nexage.admin.core.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BuyerMetadataDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Set<String> adsourceNames = new HashSet<>();
  private Set<String> externalDataProviderNames = new HashSet<>();
  private Set<String> userNames = new HashSet<>();

  public Set<String> getAdsourceNames() {
    return adsourceNames;
  }

  public void setAdsourceNames(Set<String> adsourceNames) {
    this.adsourceNames = adsourceNames;
  }

  public Set<String> getExternalDataProviderNames() {
    return externalDataProviderNames;
  }

  public void setExternalDataProviderNames(Set<String> externalDataProviderNames) {
    this.externalDataProviderNames = externalDataProviderNames;
  }

  public Set<String> getUserNames() {
    return userNames;
  }

  public void setUserNames(Set<String> userNames) {
    this.userNames = userNames;
  }

  public void addToAdsourceNames(String adsource) {
    this.adsourceNames.add(adsource);
  }

  public void addToExternalDataProviderNames(String dataProviderNames) {
    this.externalDataProviderNames.add(dataProviderNames);
  }

  public void addToUserNames(String userNames) {
    this.userNames.add(userNames);
  }
}
