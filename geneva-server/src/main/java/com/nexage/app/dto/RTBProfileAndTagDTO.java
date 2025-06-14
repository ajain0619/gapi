package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Tag;
import java.io.Serializable;

public class RTBProfileAndTagDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private RTBProfile rtbProfile;
  private Tag tag;

  @JsonCreator
  public RTBProfileAndTagDTO(
      @JsonProperty(value = "rtbProfile") RTBProfile rtbProfile,
      @JsonProperty(value = "tag") Tag tag) {
    this.rtbProfile = rtbProfile;
    this.tag = tag;
  }

  public RTBProfile getRtbProfile() {
    return rtbProfile;
  }

  public Tag getTag() {
    return tag;
  }
}
