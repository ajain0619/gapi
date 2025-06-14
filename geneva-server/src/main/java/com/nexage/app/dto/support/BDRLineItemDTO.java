package com.nexage.app.dto.support;

import java.util.HashSet;
import java.util.Set;

/**
 * Dto for BDRLineItem {@link com.nexage.admin.core.bidder.model.BDRLineItem}
 *
 * @author Eugeny Yurko
 * @since 10.09.2014
 */
public class BDRLineItemDTO extends BaseDTO {

  private Set<? extends BDRTargetGroupDTO> targetGroups = new HashSet<>();

  protected BDRLineItemDTO(Builder builder) {
    super(builder);
    targetGroups = builder.targetGroups;
  }

  public Set<? extends BDRTargetGroupDTO> getTargetGroups() {
    return targetGroups;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends BaseDTO.Builder {
    private Set<? extends BDRTargetGroupDTO> targetGroups;

    protected Builder() {
      super();
    }

    public Builder withTargetGroups(Set<? extends BDRTargetGroupDTO> targetGroups) {
      this.targetGroups = targetGroups;
      return this;
    }

    @Override
    public BDRLineItemDTO build() {
      return new BDRLineItemDTO(this);
    }
  }
}
