package com.nexage.app.dto.deal;

import com.fasterxml.jackson.annotation.JsonInclude;

/** @deprecated please use {@link com.nexage.app.dto.deals.DealSiteDTO} instead */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
public class DealSiteDTO {

  private Long pid;
  private Long sitePid;

  public DealSiteDTO() {}

  public DealSiteDTO(Builder builder) {
    this.pid = builder.pid;
    this.sitePid = builder.sitePid;
  }

  public Long getPid() {
    return pid;
  }

  public Long getSitePid() {
    return sitePid;
  }

  public static final class Builder {
    private Long pid;
    private Long sitePid;

    public Builder setPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder setSitePid(Long sitePid) {
      this.sitePid = sitePid;
      return this;
    }

    public DealSiteDTO build() {
      return new DealSiteDTO(this);
    }
  }
}
