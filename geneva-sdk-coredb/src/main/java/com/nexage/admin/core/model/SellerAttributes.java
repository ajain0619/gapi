package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nexage.admin.core.enums.CrsReviewStatusBlock;
import com.nexage.admin.core.enums.CrsSecureStatusBlock;
import com.nexage.admin.core.enums.PublisherDataProtectionRole;
import com.nexage.admin.core.enums.SellerDomainVerificationAuthLevel;
import com.nexage.admin.core.json.BigDecimalSerializer;
import com.nexage.admin.core.sparta.jpa.model.SellerType;
import com.nexage.admin.core.sparta.jpa.model.SmartExchangeAttributes;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "seller_attributes")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class SellerAttributes implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "seller_pid", nullable = false)
  @GeneratedValue(generator = "gen")
  @GenericGenerator(
      name = "gen",
      strategy = "foreign",
      parameters = @Parameter(name = "property", value = "seller"))
  @JsonIgnore
  private Long sellerPid;

  @JsonBackReference @OneToOne @PrimaryKeyJoinColumn private Company seller;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "rtb_profile", referencedColumnName = "pid")
  @JsonIgnore
  @JsonBackReference
  private RTBProfile defaultRtbProfile;

  @Column(name = "effective_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date effectiveDate;

  @Column(name = "rev_share")
  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal revenueShare;

  @Column(name = "rtb_fee")
  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal rtbFee;

  @Column(name = "default_block")
  @Type(type = "com.nexage.admin.core.custom.type.SetUserType")
  private Set<Long> defaultBlock = new HashSet<>();

  @Column(name = "default_bidder_groups")
  @Type(type = "com.nexage.admin.core.custom.type.SetUserType")
  private Set<Long> defaultBidderGroups = new HashSet<>();

  @Column(name = "default_bidders_allowlist")
  private boolean defaultBiddersAllowList;

  @Column(name = "hb_throttle")
  private boolean hbThrottleEnabled;

  @Column(name = "hb_throttle_perc")
  private Integer hbThrottlePercentage;

  @Column(name = "pfo_enabled")
  private boolean pfoEnabled = true;

  @Column(name = "site_limit")
  private Integer siteLimit;

  @Column(name = "positions_per_site_limit")
  private Integer positionsPerSiteLimit;

  @Column(name = "tags_per_position_limit")
  private Integer tagsPerPositionLimit;

  @Column(name = "campaigns_limit")
  private Integer campaignsLimit;

  @Column(name = "creatives_per_campaign_limit")
  private Integer creativesPerCampaignLimit;

  @Column(name = "bidder_libraries_limit")
  private Integer bidderLibrariesLimit;

  @Column(name = "block_libraries_limit")
  private Integer blockLibrariesLimit;

  @Column(name = "user_limit")
  private Integer userLimit;

  @Column(name = "limit_enabled")
  private boolean limitEnabled = true;

  @Column(name = "hb_price_preference")
  private int hbPricePreference;

  @Column(name = "transparency_management_enablement")
  private int transparencyMgmtEnablement;

  @Column(name = "include_pub_name")
  private Integer includePubName;

  @JsonSerialize(using = ToStringSerializer.class)
  @Column(name = "pub_alias")
  private Long pubAliasId;

  @Column(name = "pub_name_alias")
  private String pubNameAlias;

  @Column(name = "super_auction_enabled")
  private boolean superAuctionEnabled;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "crs_review_status_block")
  private CrsReviewStatusBlock reviewStatusBlock = CrsReviewStatusBlock.ALLOW_ALL;

  @Column(name = "crs_secure_status_block")
  @Enumerated(EnumType.ORDINAL)
  private CrsSecureStatusBlock secureStatusBlock;

  @Version private int version;

  @Column(name = "data_protection_first_third_party_distinction")
  private Integer publisherDataProtectionRole;

  @Column(name = "video_use_inbound_site_or_app_request_parameters")
  private Boolean videoUseInboundSiteOrApp;

  @Column(name = "adfeedback_opt_out")
  private Boolean adFeedbackOptOut;

  @Column(name = "seller_type")
  @Enumerated(EnumType.STRING)
  private SellerType sellerType;

  @Column(name = "buyer_transparency_opt_out")
  private Boolean buyerTransparencyOptOut;

  @Column(name = "revenue_group_pid")
  private Long revenueGroupPid;

  @Column(name = "domain_verification_auth_level")
  @Enumerated(EnumType.ORDINAL)
  private SellerDomainVerificationAuthLevel sellerDomainVerificationAuthLevel =
      SellerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_AND_UNCATEGORIZED;

  @Column(name = "human_opt_out")
  private Boolean humanOptOut = false;

  @Column(name = "smart_qps_enabled")
  private Boolean smartQPSEnabled = false;

  @Column(name = "external_ad_verification_sampling_rate")
  private float externalAdVerificationSamplingRate;

  @Column(name = "external_geoEdge_policy_key")
  private String externalAdVerificationPolicyKey;

  @Column(name = "dynamic_floor_enabled")
  private Boolean dynamicFloorEnabled = false;

  @Column(name = "ad_strict_approval", insertable = false, updatable = false)
  private Boolean adStrictApproval;

  @Column(name = "ssp_deal_rev_share")
  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal sspDealRevShare;

  @Column(name = "joint_deal_rev_share")
  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal jointDealRevShare;

  @Column(name = "seller_deal_rev_share")
  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal sellerDealRevShare;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sellerAttributes")
  @JsonManagedReference
  private SmartExchangeAttributes smartExchangeAttributes;

  @Column(name = "enable_ctv_selling")
  private boolean enableCtvSelling;

  @DecimalMin(value = "0.0", inclusive = false)
  @DecimalMax(value = "100.0")
  @Digits(integer = 3, fraction = 2)
  @Column(name = "creative_success_rate_threshold")
  private BigDecimal creativeSuccessRateThreshold;

  @Column(name = "creative_success_rate_threshold_opt_out")
  private boolean creativeSuccessRateThresholdOptOut = false;

  @Column(name = "raw_response")
  private boolean rawResponse = false;

  @Column(name = "human_prebid_sample_rate")
  @Min(0)
  @Max(100)
  private Integer humanPrebidSampleRate;

  @Column(name = "human_postbid_sample_rate")
  @Min(0)
  @Max(100)
  private Integer humanPostbidSampleRate;

  @Column(name = "custom_deal_floor_enabled")
  @ColumnDefault(value = "0")
  private boolean customDealFloorEnabled;

  @PrePersist
  public void prePersist() {
    effectiveDate = new Date();

    if (secureStatusBlock == null) {
      secureStatusBlock = CrsSecureStatusBlock.ALLOW_ALL;
    }
  }

  public void setPublisherDataProtectionRole(Integer publisherDataProtectionRole) {
    var role = PublisherDataProtectionRole.from(publisherDataProtectionRole);
    this.publisherDataProtectionRole = role.getExternalValue();
  }

  public Set<Long> getDefaultBlock() {
    return defaultBlock != null ? defaultBlock : new HashSet<>();
  }

  public Set<Long> getDefaultBidderGroups() {
    return defaultBidderGroups != null ? defaultBidderGroups : new HashSet<>();
  }

  public Boolean getAdStrictApproval() {
    return adStrictApproval != null ? adStrictApproval : Boolean.FALSE;
  }

  @PostLoad
  public void postLoad() {
    if (secureStatusBlock == null) {
      secureStatusBlock = CrsSecureStatusBlock.ALLOW_ALL;
    }
  }
}
