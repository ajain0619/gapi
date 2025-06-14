package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublisherBidderDTO {

  private Long pid;

  public PublisherBidderDTO() {}

  private PublisherBidderDTO(Builder builder) {
    this.pid = builder.pid;
  }

  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PublisherBidderDTO that = (PublisherBidderDTO) o;

    return !(pid != null ? !pid.equals(that.pid) : that.pid != null);
  }

  @Override
  public int hashCode() {
    return pid != null ? pid.hashCode() : 0;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Long pid;

    public Builder withPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public PublisherBidderDTO build() {
      return new PublisherBidderDTO(this);
    }
  }
}
