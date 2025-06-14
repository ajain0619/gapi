package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class BidderCreativeDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private CreativeFileReferenceDTO creativeFileRef;
  private BdrCreativeDTO creative;

  @JsonCreator
  public BidderCreativeDTO(
      @JsonProperty(value = "creativeFileRef") CreativeFileReferenceDTO creativeFileRef,
      @JsonProperty(value = "creative") BdrCreativeDTO creative) {

    this.creativeFileRef = creativeFileRef;
    this.creative = creative;
  }

  public CreativeFileReferenceDTO getCreativeFileRef() {
    return creativeFileRef;
  }

  public BdrCreativeDTO getBdrCreativeDTO() {
    return creative;
  }
}
