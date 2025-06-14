package com.nexage.app.dto.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.Sets;
import com.nexage.admin.core.bidder.model.BDRTargetGroupCreative;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.bidder.type.BDRLineItemStatus;
import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.NotNull;

/**
 * @author Nick Ilkevich
 * @since 06.10.2014
 */
public class BDRTargetGroupDetailsDTO implements Serializable {

  @NotNull @JsonUnwrapped private BdrTargetGroup targetGroup;

  private Set<AssociatedCreativeDTO> associatedCreatives = Sets.newHashSet();

  @Deprecated
  @JsonProperty("creativesPids")
  private Set<Long> creativesPids = Sets.newHashSet();

  @JsonProperty("lineItemStatus")
  private BDRLineItemStatus lineItemStatus;

  @SuppressWarnings("unused")
  public BDRTargetGroupDetailsDTO() {}

  private BDRTargetGroupDetailsDTO(Builder builder) {
    setTargetGroup(builder.targetGroup);
    setAssociatedCreatives(builder.associatedCreatives);
    setLineItemStatus(builder.lineItemStatus);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public BdrTargetGroup getTargetGroup() {
    return targetGroup;
  }

  public void setTargetGroup(BdrTargetGroup targetGroup) {
    this.targetGroup = targetGroup;
  }

  public Set<AssociatedCreativeDTO> getAssociatedCreatives() {
    return associatedCreatives;
  }

  public void setAssociatedCreatives(Set<AssociatedCreativeDTO> associatedCreatives) {
    this.associatedCreatives = associatedCreatives;
  }

  @Deprecated
  public Set<Long> getCreativesPids() {
    return creativesPids;
  }

  @Deprecated
  public void setCreativesPids(Set<Long> creativesPids) {
    this.creativesPids = creativesPids;
  }

  public BDRLineItemStatus getLineItemStatus() {
    return lineItemStatus;
  }

  public void setLineItemStatus(BDRLineItemStatus lineItemStatus) {
    this.lineItemStatus = lineItemStatus;
  }

  public static final class Builder {

    private BdrTargetGroup targetGroup;
    private Set<AssociatedCreativeDTO> associatedCreatives = Sets.newHashSet();
    private BDRLineItemStatus lineItemStatus;

    private Builder() {}

    public Builder withTargetGroup(BdrTargetGroup targetGroup) {
      this.targetGroup = targetGroup;
      return this;
    }

    public Builder withLineItemStatus() {
      if (targetGroup == null) {
        throw new IllegalArgumentException("Target hasn't been set!");
      }
      this.lineItemStatus = targetGroup.getLineItem().getStatus();
      return this;
    }

    public Builder withAssociatedCreatives(Set<BDRTargetGroupCreative> targetGroupCreatives) {
      if (targetGroupCreatives != null) {
        for (BDRTargetGroupCreative targetGroupCreative : targetGroupCreatives) {
          associatedCreatives.add(
              AssociatedCreativeDTO.newBuilder()
                  .withPid(targetGroupCreative.getCreative().getPid())
                  .withWeight(targetGroupCreative.getWeight())
                  .build());
        }
      }
      return this;
    }

    public BDRTargetGroupDetailsDTO build() {
      return new BDRTargetGroupDetailsDTO(this);
    }
  }
}
