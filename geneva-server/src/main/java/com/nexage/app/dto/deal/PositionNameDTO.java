package com.nexage.app.dto.deal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PositionNameDTO {

  private final long positionId;

  private final String positionName;

  private PositionNameDTO(Builder builder) {
    this.positionId = builder.positionId;
    this.positionName = builder.positionName;
  }

  public long getPositionId() {
    return this.positionId;
  }

  public String getPositionName() {
    return this.positionName;
  }

  public static class Builder {

    private long positionId;

    private String positionName;

    public Builder setPositionId(long positionId) {
      this.positionId = positionId;
      return this;
    }

    public Builder setPositionName(String positionName) {
      this.positionName = positionName;
      return this;
    }

    public PositionNameDTO build() {
      return new PositionNameDTO(this);
    }
  }
}
