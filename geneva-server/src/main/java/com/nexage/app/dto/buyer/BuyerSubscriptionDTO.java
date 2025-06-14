package com.nexage.app.dto.buyer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BuyerSubscriptionDTO {

  private String name;
  private Boolean requiresData;
  private String alias;

  private BuyerSubscriptionDTO() {}

  private BuyerSubscriptionDTO(Builder builder) {
    this.name = builder.name;
    this.requiresData = builder.requiresData;
    this.alias = builder.alias;
  }

  public String getName() {
    return name;
  }

  public Boolean getRequiresData() {
    return requiresData;
  }

  public String getAlias() {
    return alias;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private String name;
    private Boolean requiresData;
    private String alias;

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withRequiresData(Boolean requiresData) {
      this.requiresData = requiresData;
      return this;
    }

    public Builder withAlias(String alias) {
      this.alias = alias;
      return this;
    }

    public BuyerSubscriptionDTO build() {
      return new BuyerSubscriptionDTO(this);
    }
  }
}
