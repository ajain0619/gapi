package com.nexage.admin.core.sparta.jpa.model;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SiteMetadata implements Serializable {

  private static final long serialVersionUID = 1L;

  public String savedRtbProfileId;

  public String savedMediationTagId;

  public SiteMetadata() {}

  public String getSavedRtbProfileId() {
    return savedRtbProfileId;
  }

  public void setSavedRtbProfileId(String savedRtbProfileId) {
    this.savedRtbProfileId = savedRtbProfileId;
  }

  public String getSavedMediationTagId() {
    return savedMediationTagId;
  }

  public void setSavedMediationTagId(String savedMediationTagId) {
    this.savedMediationTagId = savedMediationTagId;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
