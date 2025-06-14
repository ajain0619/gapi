package com.nexage.app.dto;

import com.nexage.app.dto.tag.TagUpdateInfoDTO;
import java.util.Set;

public class SiteUpdateInfoDTO {

  private String txId;
  private Set<TagUpdateInfoDTO> tagUpdateInfo;

  private SiteUpdateInfoDTO() {}

  private SiteUpdateInfoDTO(Builder builder) {
    this.txId = builder.txId;
    this.tagUpdateInfo = builder.tagUpdateInfo;
  }

  public String getTxId() {
    return txId;
  }

  public Set<TagUpdateInfoDTO> getTagUpdateInfo() {
    return tagUpdateInfo;
  }

  public static final class Builder {

    private String txId;
    private Set<TagUpdateInfoDTO> tagUpdateInfo;

    public Builder setTxId(String txId) {
      this.txId = txId;
      return this;
    }

    public Builder setTagUpdateInfo(Set<TagUpdateInfoDTO> tagUpdateInfo) {
      this.tagUpdateInfo = tagUpdateInfo;
      return this;
    }

    public SiteUpdateInfoDTO build() {
      return new SiteUpdateInfoDTO(this);
    }
  }
}
