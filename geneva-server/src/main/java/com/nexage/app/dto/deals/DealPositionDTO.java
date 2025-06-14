package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DealPositionDTO {

  private Long pid;
  private Long positionPid;

  public DealPositionDTO() {}

  public DealPositionDTO(Builder builder) {
    this.pid = builder.pid;
    this.positionPid = builder.positionPid;
  }

  public Long getPid() {
    return pid;
  }

  public Long getPositionPid() {
    return positionPid;
  }

  public static final class Builder {
    private Long pid;
    private Long positionPid;

    public Builder setPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder setPositionPid(Long positionPid) {
      this.positionPid = positionPid;
      return this;
    }

    public DealPositionDTO build() {
      return new DealPositionDTO(this);
    }
  }
}
