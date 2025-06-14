package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.nexage.admin.core.bidder.support.validation.annotation.CountryLetterCodes;
import com.nexage.admin.core.custom.UserIdBidRequestTypeConverter;
import com.nexage.admin.core.enums.AdSizeFilter;
import com.nexage.admin.core.enums.BidderFormat;
import com.nexage.admin.core.enums.BillingSource;
import com.nexage.admin.core.enums.BlockListInclusion;
import com.nexage.admin.core.enums.BuyerDomainVerificationAuthLevel;
import com.nexage.admin.core.enums.ImpressionFilter;
import com.nexage.admin.core.enums.UserIdBidRequestType;
import com.nexage.admin.core.enums.UserIdPreference;
import com.nexage.admin.core.enums.VerificationType;
import com.nexage.admin.core.model.filter.BidderConfigDenyAllowFilterList;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

/** The persistent class for the bidder_config database table. */
@Audited
@Table(name = "bidder_config")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class BidderConfig implements Serializable {

  private static final long serialVersionUID = -7722862631686630786L;

  private static final String DEFAULT_BIDDER_NAME = "USA";

  @Column(name = "id", nullable = false, length = 32)
  protected String id;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  protected Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  protected Integer version;

  /**
   * Minimum charge for bid requests in dollars-CPM to override Exchange default; 0 = use the
   * Exchange default in sys-config.
   */
  @NotNull
  @Min(0)
  @Column(name = "bid_request_cpm")
  private BigDecimal bidRequestCpm;

  /** Base URL for bid requests. */
  @Column(name = "bid_request_url")
  private String bidRequestUrl;

  /**
   * Default currency for bid price. This is for future use. Initially everything will be US
   * dollars.
   */
  @Column(name = "default_bid_currency")
  private String defaultBidCurrency;

  /** Default price units of bids: 0 = USD CPM, 1 = USD per unit, 2 = USD Micros per unit. */
  @Column(name = "default_bid_unit")
  private int defaultBidUnit;

  /** Indication of allowed auction types: 0 = All, 1 = First Price, or 2 = Second Price Plus. */
  @Column(name = "filter_auction_types")
  private int auctionTypeFilter;

  /** Comma separated list of content categories (e.g., IAB1, IAB2, etc.; see openRTB). */
  @Lob
  @Column(name = "filter_categories")
  private String categoriesFilter;

  /**
   * Switch that specifies if the categories list is a block list (false) or a allow list (true).
   */
  @Column(name = "filter_categories_allowlist")
  private boolean categoriesFilterMode;

  /** Comma separated list of countries by standard abbreviations. */
  @CountryLetterCodes
  @Lob
  @Column(name = "filter_countries")
  private String countryFilter;

  /** Switch that specifies if the country list is a block list (false) or a allow list (true). */
  @Column(name = "filter_countries_allowlist")
  private boolean countryFilterMode;

  /** Comma separated list of devices. */
  @Lob
  @Column(name = "filter_devices")
  private String devicesFilter;

  /** Switch that specifies if the device list is a block list (false) or a allow list (true). */
  @Column(name = "filter_devices_allowlist")
  private boolean devicesFilterMode;

  /** A list of Publishers is the list of Seller Companies in the company table. */
  @Size(max = 10000)
  @Lob
  @Column(name = "filter_publishers")
  private String publishersFilter;

  /**
   * Switch that specifies whether the publisher list is a block list (false) or a allow list
   * (true).
   */
  @Column(name = "filter_publishers_allowlist")
  private boolean publishersFilterMode;

  /** A list of Sites is the list of Seller Companies's sites. */
  @Size(max = 1000)
  @Lob
  @Column(name = "filter_sites")
  private String sitesFilter;

  /**
   * Switch that specifies whether the sites list is a block list (false) or a allow list (true).
   */
  @Column(name = "filter_sites_allowlist")
  private boolean sitesFilterMode;

  /** Comma separated list of ad sizes */
  @Size(max = 1000)
  @Column(name = "filter_ad_sizes")
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.usertype.AdSizeFilterUserType")
  private Set<AdSizeFilter> adSizesFilter;

  /**
   * Switch that specifies whether the ad size list is a block list (false) or a allow list (true).
   */
  @Column(name = "filter_ad_sizes_allowlist")
  private boolean adSizesFilterMode;

  /** Maximum number of bid requests per second; -1 = unlimited. */
  @NotNull
  @Min(-1)
  @Column(name = "filter_request_rate")
  private Integer requestRateFilter;

  /**
   * Switch that specifies whether devices must be JavaScript capable (true) or doesn't matter
   * (false).
   */
  @Column(name = "filter_script")
  private boolean scriptAllowedFilter;

  /**
   * Switch to exclude (false) or include (true) lists (i.e., block lists of content categories,
   * advertiser domains, and ad types) in bid requests.
   */
  @Column(name = "include_block_lists")
  @Enumerated(EnumType.ORDINAL)
  private BlockListInclusion includeLists;

  /**
   * Win notice and/or ad serve URL; fully qualified with substitution macros for request ID, bid
   * ID, campaign ID, price, and price units.
   */
  @Column(name = "notice_url")
  private String noticeUrl;

  @Column(name = "ad_screening_enabled")
  @JsonIgnore
  private boolean adScreeningEnabled = false;

  /** Bid request/response format version, i.e OpenRTBv1, OpenRTBv2, etc. */
  @Column(nullable = false, name = "format_type")
  @NotNull
  @Enumerated(EnumType.STRING)
  private BidderFormat formatType;

  /** Switch to disable (false) or enable (true) bid requests being sent to this bidder. */
  @Column(name = "traffic_status")
  private boolean trafficStatus;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = false, updatable = false)
  @JsonIgnore
  private java.util.Date creationDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update")
  @JsonIgnore
  private java.util.Date lastUpdate;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "company_id",
      referencedColumnName = "pid",
      updatable = false,
      nullable = false)
  @JsonIgnore
  @NotAudited
  private Company company;

  @Column(name = "company_id", insertable = false, updatable = false)
  private Long companyPid;

  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "bidderConfig",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonIgnore
  private Set<BidderSubscription> bidderSubscriptions = Sets.newHashSet();

  @Transient private Set<SubscriptionInfo> subscriptions = Sets.newHashSet();

  /** true if the bidder wishes to only receive requests with location data */
  @Column(name = "location_enabled_only")
  private boolean locationEnabledOnly;

  /** true if the bidder wishes to only receive requests with device identifiers */
  @Column(name = "device_identified_only")
  private boolean deviceIdentifiedOnly;

  @NotAudited
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "bidder_private_attr_acl",
      joinColumns = {@JoinColumn(name = "bidder_id", nullable = false)},
      inverseJoinColumns = {@JoinColumn(name = "attribute_pid", nullable = false)})
  private Set<BidderPrivateAttribute> privateAttributes = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  @AuditJoinTable(
      name = "bidder_exchange_regional_aud",
      inverseJoinColumns = {
        @JoinColumn(name = "exchange_regional_id", referencedColumnName = "pid")
      })
  @JoinTable(
      name = "bidder_exchange_regional",
      joinColumns = {@JoinColumn(name = "bidder_id", nullable = false)},
      inverseJoinColumns = {@JoinColumn(name = "exchange_regional_id", nullable = false)})
  private Set<ExchangeRegional> exchangeRegionals = new HashSet<>();

  @Column(nullable = true)
  private String name;

  @Column(name = "allow_traffic", nullable = true)
  private String allowedTraffic;

  @Valid
  @OneToMany(
      mappedBy = "bidderConfig",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @AuditMappedBy(mappedBy = "bidderConfig")
  @JsonManagedReference
  private Set<BidderDeviceType> allowedDeviceTypes = new HashSet<>();

  @Column(name = "verification_type", nullable = true)
  private VerificationType verificationType;

  @Valid
  @OneToMany(
      mappedBy = "bidderConfig",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @AuditMappedBy(mappedBy = "bidderConfig")
  @JsonManagedReference
  private Set<BidderRegionLimit> regionLimits = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER, targetClass = NativeVersion.class)
  @CollectionTable(
      name = "native_version",
      joinColumns = @JoinColumn(name = "bidder_config_pid", referencedColumnName = "pid"))
  @Column(name = "version")
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.usertype.NativeVersionUserType")
  private Set<NativeVersion> nativeVersions = new HashSet<>();

  @Column(name = "filter_impr")
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.usertype.ImpressionFilterUserType")
  private Set<ImpressionFilter> imprFilter;

  @Column(name = "bidrequest_userid_preference")
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.usertype.UserIdPreferenceUserType")
  private UserIdPreference userIdPreference = UserIdPreference.NO_ID_RESTRICTION;

  @Column(name = "billing_src")
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.usertype.BillingSourceUserType")
  private BillingSource billingSource;

  @Column(name = "header_bidding_enabled")
  private Boolean headerBiddingEnabled = false;

  @Column(name = "uump_global_id")
  private Long uumpGlobalBidderPid;

  @Column(name = "domain_filter_allow_list")
  private Boolean domainFilterAllowList = false;

  @Column(name = "app_bundle_filter_allow_list")
  private boolean appBundleFilterAllowList = false;

  @Column(name = "domain_filter_allow_unknown_urls")
  private Boolean domainFilterAllowUnknownUrls = true;

  @Column(name = "app_bundle_filter_allow_unknown_apps")
  private boolean appBundleFilterAllowUnknownApps = true;

  @Column(name = "allow_bridgeId_match", nullable = false)
  @ColumnDefault("false")
  private Boolean allowBridgeIdMatch = false;

  @Column(name = "allow_connect_id", nullable = false)
  @ColumnDefault("false")
  private Boolean allowConnectId = false;

  @Column(name = "allow_id_graph_match", nullable = false)
  @ColumnDefault("true")
  private Boolean allowIdGraphMatch = true;

  @Column(name = "allow_liveramp", nullable = false)
  @ColumnDefault("false")
  private Boolean allowLiveramp = false;

  @Column(name = "domain_verification_auth_level")
  @Enumerated(EnumType.ORDINAL)
  private BuyerDomainVerificationAuthLevel domainVerificationAuthLevel =
      BuyerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_UNCATEGORIZED;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @AuditJoinTable(name = "bidder_identity_provider_aud")
  @JoinTable(
      name = "bidder_identity_provider",
      joinColumns = {
        @JoinColumn(name = "bidder_pid", referencedColumnName = "pid", nullable = false)
      },
      inverseJoinColumns = {
        @JoinColumn(name = "identity_provider_pid", referencedColumnName = "pid", nullable = false)
      })
  private Set<IdentityProviderView> identityProviders = new HashSet<>();

  @OneToMany(
      mappedBy = "bidderConfig",
      fetch = FetchType.EAGER,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  @JsonManagedReference
  private Set<BidderConfigDenyAllowFilterList> bidderConfigDenyAllowFilterLists = new HashSet<>();
  /**
   * Indicates whether or not the bidder should receive eligible deal sizes as an extension on the
   * bid request
   */
  @Column(name = "send_deal_sizes", nullable = false)
  @ColumnDefault("false")
  private Boolean sendDealSizes = false;

  @Column(name = "userId_bid_request_type")
  @Convert(converter = UserIdBidRequestTypeConverter.class)
  private UserIdBidRequestType userIdBidRequestType = UserIdBidRequestType.UNKNOWN;

  /** Stores allowed content encoding values */
  @Column(name = "allowed_content_encoding")
  private String allowedContentEncoding;

  @Column(name = "smart_qps_outbound_enabled", nullable = false)
  @ColumnDefault("false")
  private Boolean smartQpsOutboundEnabled;

  @Column(name = "throttle_rate", nullable = false)
  @ColumnDefault("0.0000")
  private Double throttleRate;

  @Builder
  @Getter
  @ToString(onlyExplicitlyIncluded = true)
  public static final class SubscriptionInfo {

    @ToString.Include private final long dataProviderPid;
    @ToString.Include private final boolean requiresDataToBid;
    @ToString.Include private final String bidderAlias;

    @JsonCreator
    public SubscriptionInfo(
        @JsonProperty(value = "dataProviderPid") long dataProviderPid,
        @JsonProperty(value = "requiresDataToBid") Boolean bidFlag,
        @JsonProperty(value = "bidderAlias") String bidderAlias) {
      this.dataProviderPid = dataProviderPid;
      if (null != bidFlag) {
        requiresDataToBid = bidFlag;
      } else {
        requiresDataToBid = false; // default
      }
      this.bidderAlias = bidderAlias;
    }
  }

  public Set<SubscriptionInfo> getSubscriptions() {
    if (subscriptions == null || subscriptions.isEmpty()) {
      subscriptions = getSubscriptionInfo();
    }
    return subscriptions;
  }

  /**
   * @return list of objects used for bidder subscription info in nexage-cache BidderConfig object
   */
  @JsonIgnore
  public Set<SubscriptionInfo> getSubscriptionInfo() {
    Set<SubscriptionInfo> infoset = new HashSet<>();
    for (BidderSubscription subscription : bidderSubscriptions) {
      SubscriptionInfo info =
          new SubscriptionInfo(
              subscription.getExternalDataProvider().getPid(),
              subscription.isRequiresDataToBid(),
              subscription.getBidderAlias());
      infoset.add(info);
    }
    return infoset;
  }

  /** Invoked before insert to set the current time. */
  @PrePersist
  public void prePersist() {
    this.creationDate = Calendar.getInstance().getTime();
    checkAndFixFilters();
    if (getFormatType() == null) {
      setFormatType(BidderFormat.OpenRTBv2);
    }
    if (getName() == null) {
      setName(DEFAULT_BIDDER_NAME);
    }
    if (getAllowedDeviceTypes() == null) {
      setAllowedDeviceTypes(new HashSet<>());
    }
    checkAndFixSmartQpsFields();
  }

  /** Invoked before insert to set the current time. */
  @PreUpdate
  public void preUpdate() {
    this.lastUpdate = Calendar.getInstance().getTime();
    checkAndFixFilters();
    checkAndFixSmartQpsFields();
  }

  public Set<AdSizeFilter> getAdSizesFilter() {
    if (adSizesFilter == null) {
      adSizesFilter = EnumSet.noneOf(AdSizeFilter.class);
    }
    return adSizesFilter;
  }

  public Set<ImpressionFilter> getImprFilter() {
    if (imprFilter == null) {
      imprFilter = EnumSet.noneOf(ImpressionFilter.class);
    }
    return imprFilter;
  }

  public void setUserIdPreference(UserIdPreference userIdPreference) {
    if (userIdPreference != null) {
      this.userIdPreference = userIdPreference;
    }
  }

  public void setUserIdBidRequestType(UserIdBidRequestType userIdBidRequestType) {
    this.userIdBidRequestType =
        Objects.requireNonNullElse(userIdBidRequestType, UserIdBidRequestType.UNKNOWN);
  }

  public void setAllowConnectId(Boolean allowConnectId) {
    this.allowConnectId = allowConnectId;
  }

  @JsonManagedReference
  @Deprecated
  public Set<BidderConfigDenyAllowFilterList> getBidderConfigBlackWhiteFilterLists() {
    return bidderConfigDenyAllowFilterLists;
  }

  @Deprecated
  public void setBidderConfigBlackWhiteFilterLists(
      Set<BidderConfigDenyAllowFilterList> filterLists) {
    setBidderConfigDenyAllowFilterLists(filterLists);
  }

  private void checkAndFixFilters() {
    if (StringUtils.isBlank(getCategoriesFilter())) {
      setCategoriesFilter(null);
    }
    if (StringUtils.isBlank(getCountryFilter())) {
      setCountryFilter(null);
    }
    if (StringUtils.isBlank(getDevicesFilter())) {
      setDevicesFilter(null);
    }
  }

  private void checkAndFixSmartQpsFields() {
    if (smartQpsOutboundEnabled == null) {
      smartQpsOutboundEnabled = false;
    }

    if (throttleRate == null) {
      throttleRate = 0.0;
    }
  }
}
