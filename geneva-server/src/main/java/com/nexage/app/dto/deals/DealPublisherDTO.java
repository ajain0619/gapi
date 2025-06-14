package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DealPublisherDTO {

  private Long pid;
  private Long publisherPid;

  public DealPublisherDTO() {}

  private DealPublisherDTO(Builder builder) {
    this.pid = builder.pid;
    this.publisherPid = builder.publisherPid;
  }

  public Long getPid() {
    return pid;
  }

  public Long getPublisherPid() {
    return publisherPid;
  }

  public static final class Builder {

    private Long pid;
    private Long publisherPid;

    public Builder setPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder setPublisherPid(Long pid) {
      this.publisherPid = pid;
      return this;
    }

    public DealPublisherDTO build() {
      return new DealPublisherDTO(this);
    }
  }
}
