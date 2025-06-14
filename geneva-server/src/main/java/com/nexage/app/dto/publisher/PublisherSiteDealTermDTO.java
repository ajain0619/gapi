package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonInclude(Include.NON_NULL)
public class PublisherSiteDealTermDTO implements Serializable {

  private BigDecimal nexageRevenueShare;
  private BigDecimal rtbFee;

  public PublisherSiteDealTermDTO() {}

  public PublisherSiteDealTermDTO(Builder builder) {
    this.nexageRevenueShare = builder.nexageRevenueShare;
    this.rtbFee = builder.rtbFee;
  }

  public BigDecimal getNexageRevenueShare() {
    return nexageRevenueShare;
  }

  public void setNexageRevenueShare(BigDecimal nexageRevenueShare) {
    this.nexageRevenueShare = nexageRevenueShare;
  }

  public BigDecimal getRtbFee() {
    return rtbFee;
  }

  public void setRtbFee(BigDecimal rtbFee) {
    this.rtbFee = rtbFee;
  }

  @JsonIgnore
  public boolean isEmpty() {
    return nexageRevenueShare == null && rtbFee == null;
  }

  @JsonIgnore
  public boolean isNotEmpty() {
    return !isEmpty();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private BigDecimal nexageRevenueShare;
    private BigDecimal rtbFee;

    public Builder withNexageRevenueShare(BigDecimal nexageRevenueShare) {
      this.nexageRevenueShare = nexageRevenueShare;
      return this;
    }

    public Builder withRtbFee(BigDecimal rtbFee) {
      this.rtbFee = rtbFee;
      return this;
    }

    public PublisherSiteDealTermDTO build() {
      return new PublisherSiteDealTermDTO(this);
    }
  }
}
