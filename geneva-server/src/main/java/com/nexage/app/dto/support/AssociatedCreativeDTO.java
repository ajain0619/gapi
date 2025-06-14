package com.nexage.app.dto.support;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.nexage.admin.core.bidder.model.HasCreativeWeight;
import java.io.Serializable;

/**
 * @author Eugeny Yurko
 * @since 21.10.2014
 */
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    creatorVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class AssociatedCreativeDTO implements Serializable, HasCreativeWeight {

  private Long pid;
  private double weight;

  @SuppressWarnings("unused")
  public AssociatedCreativeDTO() {}

  private AssociatedCreativeDTO(Builder builder) {
    pid = builder.pid;
    weight = builder.weight;
  }

  public Long getPid() {
    return pid;
  }

  @Override
  public Long getCreativePid() {
    return pid;
  }

  public double getWeight() {
    return weight;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Long pid;
    private double weight;

    private Builder() {}

    public Builder withPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder withWeight(double weight) {
      this.weight = weight;
      return this;
    }

    public AssociatedCreativeDTO build() {
      return new AssociatedCreativeDTO(this);
    }
  }
}
