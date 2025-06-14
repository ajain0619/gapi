package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.TierType;
import java.math.BigDecimal;

@JsonInclude(Include.NON_NULL)
public class PubTagHierarchyDTO {

  private Long tagPid;
  private String tagname;
  /** @deprecated use the inclusive term {@link #filterBiddersAllowlist} instead. */
  @Deprecated(since = "SSP-22461", forRemoval = true)
  private Boolean filterBiddersWhitelist;

  private Boolean filterBiddersAllowlist;
  private BigDecimal revenue;
  private Long tierPid;
  private Integer tierLevel;
  private TierType tierType;
  private Boolean belongsToRTBGroup;
  private Boolean blockedAsDefault;

  private PubTagHierarchyDTO() {}

  private PubTagHierarchyDTO(Builder builder) {
    this.tagPid = builder.tagPid;
    this.tagname = builder.tagname;
    this.filterBiddersWhitelist =
        builder.filterBiddersAllowlist != null
            ? builder.filterBiddersAllowlist
            : builder.filterBiddersWhitelist;
    this.filterBiddersAllowlist =
        builder.filterBiddersAllowlist != null
            ? builder.filterBiddersAllowlist
            : builder.filterBiddersWhitelist;
    this.revenue = builder.revenue;
    this.tierPid = builder.tierPid;
    this.tierLevel = builder.tierLevel;
    this.belongsToRTBGroup = builder.belongsToRTBGroup;
    this.blockedAsDefault = builder.blockedAsDefault;
    this.tierType = builder.tierType;
  }

  public String getTagname() {
    return tagname;
  }

  public Long getTagPid() {
    return tagPid;
  }

  /** @deprecated use the inclusive term {@link #filterBiddersAllowlist} instead. */
  @Deprecated(since = "SSP-22461", forRemoval = true)
  public Boolean getFilterBiddersWhitelist() {
    return filterBiddersAllowlist != null ? filterBiddersAllowlist : filterBiddersWhitelist;
  }

  public Boolean getFilterBiddersAllowlist() {
    return filterBiddersAllowlist != null ? filterBiddersAllowlist : filterBiddersWhitelist;
  }

  public BigDecimal getRevenue() {
    return revenue;
  }

  public void setRevenue(BigDecimal revenue) {
    this.revenue = revenue;
  }

  public Integer getTierLevel() {
    return tierLevel;
  }

  public TierType getTierType() {
    return tierType;
  }

  public Long getTierPid() {
    return tierPid;
  }

  public Boolean getBelongsToRTBGroup() {
    return belongsToRTBGroup;
  }

  public Boolean getBlockedAsDefault() {
    return blockedAsDefault;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Long tagPid;
    private String tagname;
    private Boolean filterBiddersWhitelist;
    private Boolean filterBiddersAllowlist;
    private BigDecimal revenue;
    private Long tierPid;
    private Integer tierLevel;
    private Boolean belongsToRTBGroup;
    private Boolean blockedAsDefault;
    private TierType tierType;

    public Builder withTagPid(Long pid) {
      this.tagPid = pid;
      return this;
    }

    public Builder withTagname(String tagname) {
      this.tagname = tagname;
      return this;
    }

    /** @deprecated use the inclusive term {@link #filterBiddersAllowlist} instead. */
    @Deprecated(since = "SSP-22461", forRemoval = true)
    public Builder withFilterBiddersWhitelist(Boolean filterBiddersWhitelist) {
      if (this.filterBiddersAllowlist != null) {
        this.filterBiddersWhitelist = this.filterBiddersAllowlist;
      } else if (filterBiddersWhitelist != null) {
        this.filterBiddersWhitelist = filterBiddersWhitelist;
        this.filterBiddersAllowlist = filterBiddersWhitelist;
      }
      return this;
    }

    public Builder withFilterBiddersAllowlist(Boolean filterBiddersAllowlist) {
      // To give precedence to allowlist over whitelist
      if (filterBiddersAllowlist != null
          && (this.filterBiddersAllowlist == null
              || this.filterBiddersWhitelist == null
              || !this.filterBiddersAllowlist.equals(filterBiddersAllowlist))) {
        this.filterBiddersWhitelist = filterBiddersAllowlist;
        this.filterBiddersAllowlist = filterBiddersAllowlist;
      }
      return this;
    }

    public Builder withRevenue(BigDecimal revenue) {
      this.revenue = revenue;
      return this;
    }

    public Builder withTierPid(Long tierPid) {
      this.tierPid = tierPid;
      return this;
    }

    public Builder withTierLevel(Integer tierLevel) {
      this.tierLevel = tierLevel;
      return this;
    }

    public Builder withTierType(TierType tierType) {
      this.tierType = tierType;
      return this;
    }

    public Builder withBelongsToRTBGroup(Boolean belongsToRTBGroup) {
      this.belongsToRTBGroup = belongsToRTBGroup;
      return this;
    }

    public PubTagHierarchyDTO build() {
      return new PubTagHierarchyDTO(this);
    }

    public Builder withBlockAsDefault(Boolean isDefaultGroup) {
      this.blockedAsDefault = isDefaultGroup;
      return this;
    }
  }
}
