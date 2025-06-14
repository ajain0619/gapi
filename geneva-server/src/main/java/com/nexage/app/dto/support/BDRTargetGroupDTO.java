package com.nexage.app.dto.support;

import com.nexage.admin.core.bidder.model.BdrTargetGroup;

/**
 * Dto for BdrTargetGroup {@link BdrTargetGroup}
 *
 * @author Eugeny Yurko
 * @since 10.09.2014
 */
public class BDRTargetGroupDTO extends BaseDTO {

  protected BDRTargetGroupDTO(Builder builder) {
    super(builder);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends BaseDTO.Builder {
    protected Builder() {
      super();
    }

    @Override
    public BDRTargetGroupDTO build() {
      return new BDRTargetGroupDTO(this);
    }
  }
}
