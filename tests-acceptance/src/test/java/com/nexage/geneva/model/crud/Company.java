package com.nexage.geneva.model.crud;

public class Company {
  private String pid;
  private String id;
  private String name;
  private String type;
  private String description;
  private String website;
  private String url; // DB field for website
  private String contactId;
  private String globalAliasName;
  private String hasHeaderBiddingSites;
  private String dhReportingId;
  private String currency;
  private Boolean thirdPartyFraudDetectionEnabled;

  public String getHasHeaderBiddingSites() {
    return hasHeaderBiddingSites;
  }

  public void setHasHeaderBiddingSites(String hasHeaderBiddingSites) {
    this.hasHeaderBiddingSites = hasHeaderBiddingSites;
  }

  public String getContactId() {
    return contactId;
  }

  public void setContactId(String contactId) {
    this.contactId = contactId;
  }

  public String getPid() {
    return pid;
  }

  public String getId() {
    return id;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getGlobalAliasName() {
    return globalAliasName;
  }

  public void setGlobalAliasName(String globalAliasName) {
    this.globalAliasName = globalAliasName;
  }

  public String getDhReportingId() {
    return dhReportingId;
  }

  public void setDhReportingId(String dhReportingId) {
    this.dhReportingId = dhReportingId;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Boolean getThirdPartyFraudDetectionEnabled() {
    return thirdPartyFraudDetectionEnabled;
  }

  public void setThirdPartyFraudDetectionEnabled(Boolean thirdPartyFraudDetectionEnabled) {
    this.thirdPartyFraudDetectionEnabled = thirdPartyFraudDetectionEnabled;
  }

  @Override
  public int hashCode() {
    int result = pid != null ? pid.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (website != null ? website.hashCode() : 0);
    result = 31 * result + (url != null ? url.hashCode() : 0);
    result = 31 * result + (contactId != null ? contactId.hashCode() : 0);
    result = 31 * result + (globalAliasName != null ? globalAliasName.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    } else {
      Company c = (Company) obj;
      if (this.getType().equals(c.getType())
          && this.getName().equals(c.getName())
          && this.getPid().equals(c.getPid())
          && this.getDescription().equals(c.getDescription())
          && this.getThirdPartyFraudDetectionEnabled()
              .equals(c.getThirdPartyFraudDetectionEnabled())
          && this.getWebsite().equals(c.getUrl())) {
        return true;
      } else return false;
    }
  }
}
