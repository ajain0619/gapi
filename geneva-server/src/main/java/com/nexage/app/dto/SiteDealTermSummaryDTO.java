package com.nexage.app.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nexage.admin.core.json.BigDecimalSerializer;
import java.math.BigDecimal;

public class SiteDealTermSummaryDTO {

  private String siteName;
  private Long sitePid;

  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal revShare;

  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal rtbFee;

  private SiteDealTermSummaryDTO() {}

  private SiteDealTermSummaryDTO(Builder builder) {
    this.siteName = builder.siteName;
    this.sitePid = builder.sitePid;
    this.revShare = builder.revShare;
    this.rtbFee = builder.rtbFee;
  }

  public String getSiteName() {
    return siteName;
  }

  public Long getSitePid() {
    return sitePid;
  }

  public BigDecimal getRevShare() {
    return revShare;
  }

  public BigDecimal getRtbFee() {
    return rtbFee;
  }

  public static final class Builder {

    private String siteName;
    private Long sitePid;
    private BigDecimal revShare;
    private BigDecimal rtbFee;

    public Builder setSiteName(String siteName) {
      this.siteName = siteName;
      return this;
    }

    public Builder setSitePid(Long sitePid) {
      this.sitePid = sitePid;
      return this;
    }

    public Builder setRevShare(BigDecimal revShare) {
      this.revShare = revShare;
      return this;
    }

    public Builder setRtbFee(BigDecimal rtbFee) {
      this.rtbFee = rtbFee;
      return this;
    }

    public SiteDealTermSummaryDTO build() {
      return new SiteDealTermSummaryDTO(this);
    }
  }
}
