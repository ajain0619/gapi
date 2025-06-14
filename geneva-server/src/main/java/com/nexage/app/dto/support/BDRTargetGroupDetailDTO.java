package com.nexage.app.dto.support;

import com.nexage.admin.core.bidder.model.BdrTargetGroup;

/**
 * Dto for BdrTargetGroup {@link BdrTargetGroup}
 *
 * @author Eugeny Yurko
 * @since 19.09.2014
 */
public class BDRTargetGroupDetailDTO extends BDRTargetGroupDTO {

  private int status;
  private int creativesCount;

  protected BDRTargetGroupDetailDTO(Builder builder) {
    super(builder);
    status = builder.status;
    creativesCount = builder.creativesCount;
  }

  public int getStatus() {
    return status;
  }

  public int getCreativesCount() {
    return creativesCount;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends BDRTargetGroupDTO.Builder {
    private int status;
    private int creativesCount;

    private Builder() {
      super();
    }

    public Builder withStatus(int status) {
      this.status = status;
      return this;
    }

    public Builder withCreativesCount(int creativesCount) {
      this.creativesCount = creativesCount;
      return this;
    }

    @Override
    public BDRTargetGroupDetailDTO build() {
      return new BDRTargetGroupDetailDTO(this);
    }
  }
}
