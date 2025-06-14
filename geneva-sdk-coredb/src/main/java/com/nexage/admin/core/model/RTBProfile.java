package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.json.MapSerializer;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileBidder;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import com.nexage.admin.core.util.UUIDGenerator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/** The persistent class for the exchange_site_tag database table. */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Audited
@Table(name = "exchange_site_tag")
public class RTBProfile implements Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  @Column(name = "tag_id", nullable = false, updatable = false, unique = true)
  @Size(max = 32)
  @JsonProperty("id")
  @EqualsAndHashCode.Include
  private String exchangeSiteTagId;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Include
  private Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  private Integer version;

  @Column(name = "description")
  @EqualsAndHashCode.Include
  private String description;

  @Column(name = "name")
  private String name;

  @Column(name = "site_alias")
  private Long siteAlias;

  @Column(name = "site_name_alias")
  private String siteNameAlias;

  @Column(name = "site_type")
  private char siteType;

  @Column(name = "pub_alias")
  private Long pubAlias;

  @Column(name = "pub_name_alias")
  private String pubNameAlias;

  @Column(name = "auction_type")
  private int auctionType;

  @Column(name = "blocked_ad_categories")
  private String blockedAdCategories;

  @Column(name = "blocked_ad_types")
  private String blockedAdTypes;

  @Column(name = "blocked_advertisers")
  private String blockedAdvertisers;

  @Column(name = "blocked_attributes")
  private String blockedAttributes;

  @Column(name = "default_reserve")
  private BigDecimal defaultReserve;

  @Column(name = "pub_net_reserve")
  @DecimalMin("0.0")
  private BigDecimal pubNetReserve;

  @Column(name = "include_consumer_id")
  private boolean includeConsumerId;

  @Column(name = "include_consumer_profile")
  private boolean includeConsumerProfile;

  @Column(name = "include_domain_references")
  private boolean includeDomainReferences;

  // TODO convert to enumeration: None, RealName,	Aliases;
  @Column(name = "include_site_name")
  private Integer includeSiteName;

  @Column(name = "low_reserve")
  private BigDecimal lowReserve;

  @Column(name = "pub_net_low_reserve")
  private BigDecimal pubNetLowReserve;

  /** Comma separated list of bidder pids */
  @Column(name = "filter_bidders")
  @JsonIgnore
  private String bidderFilterList;

  @Transient @JsonIgnore private String bidderNamesFilterList;

  /** @deprecated use the inclusive term {@link #biddersFilterAllowlist} instead. */
  @Column(name = "filter_bidders_whitelist")
  @Deprecated(since = "SSP-17992", forRemoval = true)
  private Boolean biddersFilterWhitelist;

  @Column(name = "filter_bidders_allowlist")
  private Boolean biddersFilterAllowlist;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = false, updatable = false)
  @NotNull
  private Date creationDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update")
  private Date lastUpdate;

  @Column(name = "blocked_external_data_providers")
  @JsonIgnore
  private String blockedExternalDataProviders;

  @Column(name = "site_pid", insertable = false, updatable = false)
  @NotAudited
  private Long sitePid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "site_pid", referencedColumnName = "pid")
  @JsonIgnore
  @JsonBackReference
  @ToString.Exclude
  private Site site;

  @Type(type = "com.nexage.admin.core.custom.type.MapUserType")
  @Formula(
      value =
          "(select group_concat(concat(dp.pid, '=', dp.name)) from external_data_provider dp where find_in_set(dp.pid,blocked_external_data_providers))")
  @NotAudited
  @JsonSerialize(using = MapSerializer.class)
  @ToString.Exclude
  private Map<Long, String> blockedExternalDataProviderMap = new HashMap<>();

  @Transient private boolean isDeletable;

  @Type(type = "com.nexage.admin.core.custom.type.MapUserType")
  @Formula(
      value =
          "(select group_concat(concat(bc.pid, '=', c.name)) from bidder_config bc inner join company c on bc.company_id=c.pid where find_in_set(bc.pid,filter_bidders))")
  @NotAudited
  @JsonSerialize(using = MapSerializer.class)
  @ToString.Exclude
  private Map<Long, String> bidderFilterMap = new HashMap<>();

  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "rtbprofile",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonIgnore
  @ToString.Exclude
  private Set<RTBProfileLibraryAssociation> libraries = Sets.newHashSet();

  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "rtbprofile",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonIgnore
  @ToString.Exclude
  private Set<RTBProfileBidder> bidderSeatWhitelists = Sets.newHashSet();

  @Column(name = "include_pub_name")
  @ToString.Exclude
  private Integer includePubName;

  @Column(name = "default_rtb_profile_owner_company_pid", insertable = false, updatable = false)
  @NotAudited
  private Long defaultRtbProfileOwnerCompanyPid;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "default_rtb_profile_owner_company_pid", referencedColumnName = "pid")
  @JsonIgnore
  @ToString.Exclude
  private Company ownerCompany;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_pid", referencedColumnName = "pid")
  @JsonIgnore
  @JsonBackReference
  @ToString.Exclude
  private Tag tag;

  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  @ToString.Exclude
  private Status status = Status.ACTIVE;

  @Transient private Set<Long> libraryPids;

  @Column(name = "screening_level")
  @Enumerated(EnumType.ORDINAL)
  @ToString.Exclude
  private ScreeningLevel screeningLevel;

  @Column(name = "include_geo_data")
  private boolean includeGeoData = true;

  @Column(name = "use_default_block")
  private boolean useDefaultBlock = true;

  @Column(name = "use_default_bidders")
  private boolean useDefaultBidders = true;

  @Column(name = "alter_reserve", nullable = false)
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.custom.type.AlterReserveEnumType")
  @ToString.Exclude
  private AlterReserve alterReserve = AlterReserve.OFF;

  /**
   * @param biddersFilterWhitelist the isFilterWhitelist to set /** @deprecated use the inclusive
   *     term {@link #biddersFilterAllowlist} instead. *
   */
  @Deprecated(since = "SSP-17992", forRemoval = true)
  public void setBiddersFilterWhitelist(Boolean biddersFilterWhitelist) {
    if (this.biddersFilterAllowlist != null) {
      this.biddersFilterWhitelist = this.biddersFilterAllowlist;
    } else if (biddersFilterWhitelist != null) {
      this.biddersFilterWhitelist = biddersFilterWhitelist;
      this.biddersFilterAllowlist = biddersFilterWhitelist;
    }
  }

  /** @deprecated use the inclusive term {@link #biddersFilterAllowlist} instead. */
  @Deprecated(since = "SSP-17992", forRemoval = true)
  public Boolean getBiddersFilterWhitelist() {
    return biddersFilterAllowlist != null ? biddersFilterAllowlist : biddersFilterWhitelist;
  }

  public void setBiddersFilterAllowlist(Boolean biddersFilterAllowlist) {
    if ((this.biddersFilterAllowlist == null
            || this.biddersFilterWhitelist == null
            || !this.biddersFilterAllowlist.equals(biddersFilterAllowlist))
        && biddersFilterAllowlist != null) {
      this.biddersFilterAllowlist = biddersFilterAllowlist;
      this.biddersFilterWhitelist = biddersFilterAllowlist;
    }
  }

  public Boolean getBiddersFilterAllowlist() {
    return biddersFilterAllowlist != null ? biddersFilterAllowlist : biddersFilterWhitelist;
  }

  @JsonIgnore
  public Integer getScreeningLevelValue() {
    return screeningLevel.getValue();
  }

  public void setBlockedExternalDataProviderMap(Map<Long, String> blockedExternalDataProviderMap) {
    this.blockedExternalDataProviderMap = blockedExternalDataProviderMap;
    if (blockedExternalDataProviderMap != null
        && !blockedExternalDataProviderMap.isEmpty()
        && blockedExternalDataProviders == null) {
      blockedExternalDataProviders = Joiner.on(",").join(blockedExternalDataProviderMap.keySet());
    }
  }

  public void setBidderFilterMap(Map<Long, String> bidderFilterMap) {
    this.bidderFilterMap = bidderFilterMap;
    if (bidderFilterMap != null && !bidderFilterMap.isEmpty() && bidderFilterList == null) {
      bidderFilterList = Joiner.on(",").join(bidderFilterMap.keySet());
    }
  }

  public void resetRtbProfileLibraries() {
    libraries = Sets.newHashSet();
  }

  @PreUpdate
  public void onUpdate() {
    lastUpdate = Calendar.getInstance().getTime();
  }

  @PrePersist
  public void onCreate() {
    Date d = Calendar.getInstance().getTime();
    creationDate = d;
    lastUpdate = d;
    if (StringUtils.isBlank(blockedAdTypes)) blockedAdTypes = null;
    if (StringUtils.isBlank(blockedAdCategories)) blockedAdCategories = null;
    if (StringUtils.isBlank(blockedAdvertisers)) blockedAdvertisers = null;
    if (StringUtils.isBlank(blockedAttributes)) blockedAttributes = null;
    if (StringUtils.isBlank(exchangeSiteTagId)) {
      exchangeSiteTagId = new UUIDGenerator().generateUniqueId();
    }
  }

  @Getter
  public enum ScreeningLevel {
    AllowAll(0),
    RequireDomainAndImage(1),
    RequireDomain(2),
    RequireImage(3),
    RequireDomainOrImage(4);

    private final int value;

    ScreeningLevel(int value) {
      this.value = value;
    }
  }

  @Override
  public RTBProfile clone() throws CloneNotSupportedException {
    RTBProfile clone = (RTBProfile) super.clone();

    // N.B. - Entities are still shallow-copied
    clone.setCreationDate(new Date(this.getCreationDate().getTime()));
    clone.setLastUpdate(new Date(this.getLastUpdate().getTime()));
    clone.setBlockedExternalDataProviderMap(
        new HashMap<>(this.getBlockedExternalDataProviderMap()));
    clone.setBidderFilterMap(new HashMap<>(this.getBidderFilterMap()));
    clone.setLibraryPids(new HashSet<>(this.getLibraryPids()));
    clone.setBidderSeatWhitelists(new HashSet<>(this.getBidderSeatWhitelists()));
    clone.setLibraries(new HashSet<>(this.getLibraries()));

    return clone;
  }
}
