package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.app.dto.SiteType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublisherEligibleBiddersDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long pid;

  private Integer version;

  private Set<PublisherBidderDTO> bidderGroups = new HashSet<>();

  public PublisherEligibleBiddersDTO() {}

  private PublisherEligibleBiddersDTO(Builder builder) {
    this.pid = builder.pid;
    this.version = builder.version;
    this.bidderGroups = builder.bidderGroups;
  }

  public Long getPid() {
    return pid;
  }

  public Integer getVersion() {
    return version;
  }

  public Set<PublisherBidderDTO> getBidderGroups() {
    return bidderGroups;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public void setBidderGroups(Set<PublisherBidderDTO> bidderGroups) {
    this.bidderGroups = bidderGroups;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PublisherEligibleBiddersDTO that = (PublisherEligibleBiddersDTO) o;

    if (pid != null ? !pid.equals(that.pid) : that.pid != null) return false;
    if (version != null ? !version.equals(that.version) : that.version != null) return false;
    return !(bidderGroups != null
        ? !bidderGroups.equals(that.bidderGroups)
        : that.bidderGroups != null);
  }

  @Override
  public int hashCode() {
    int result = pid != null ? pid.hashCode() : 0;
    result = 31 * result + (version != null ? version.hashCode() : 0);
    result = 31 * result + (bidderGroups != null ? bidderGroups.hashCode() : 0);
    return result;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private Long pid;
    private Integer version;
    private Set<SiteType> siteType;
    private Set<PublisherBidderDTO> bidderGroups;

    public Builder withPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder withVersion(Integer version) {
      this.version = version;
      return this;
    }

    public Builder withSiteType(Set<SiteType> siteType) {
      this.siteType = siteType;
      return this;
    }

    public Builder withBidders(Set<Long> bidders) {
      this.bidderGroups = new HashSet<>();
      for (Long bidder : bidders) {
        this.bidderGroups.add(PublisherBidderDTO.newBuilder().withPid(bidder).build());
      }
      return this;
    }

    public PublisherEligibleBiddersDTO build() {
      return new PublisherEligibleBiddersDTO(this);
    }
  }
}
