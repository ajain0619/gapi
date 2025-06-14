package com.nexage.admin.core.model;

import static java.lang.Boolean.TRUE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Sets;
import com.nexage.admin.core.bidder.model.BDRCredit;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.json.BigDecimalSerializer;
import com.nexage.admin.core.util.UUIDGenerator;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Audited
// Unique constraints are enforced by name and type as there can be company of same name in
// different fields (i.e. type).
@Table(name = "company", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "type"}))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company extends BaseModel {

  private static final long serialVersionUID = 1260588399883015972L;

  @Column(name = "name", nullable = false, length = 100)
  @NotNull
  @EqualsAndHashCode.Include
  private String name;

  @Column(name = "type", nullable = false, length = 50)
  @NotNull
  @Enumerated(EnumType.STRING)
  @EqualsAndHashCode.Include
  private CompanyType type;

  @Column(name = "url", length = 100, nullable = false)
  @NotNull
  @EqualsAndHashCode.Include
  private String website;

  @Column(name = "description")
  private String description;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contact_id", referencedColumnName = "pid")
  @NotAudited
  @JsonIgnore
  private User contact;

  @Column(name = "contact_id", insertable = false, updatable = false)
  private Long contactUserPid;

  @Column(name = "allow_ad_serving")
  @Getter(AccessLevel.NONE)
  private Boolean adServingEnabled;

  @Column(name = "dynamic_buyer_registration_enabled", nullable = false)
  private boolean dynamicBuyerRegistrationEnabled;

  @Column(name = "brxd_buyer_id_enabled_on_bid_request", nullable = false)
  private boolean brxdBuyerIdEnabledOnBidRequest;

  @Column(name = "restrict_drill_down")
  @Getter(AccessLevel.NONE)
  private Boolean restrictDrillDown;

  @Column(name = "direct_ad_serving_fee")
  private Double directAdServingFee;

  @Column(name = "house_ad_serving_fee")
  private Double houseAdServingFee;

  @Column(name = "non_remnant_house_ad_cap")
  private Double nonRemnantHouseAdCap;

  @Column(name = "house_ad_overage_fee")
  private Double houseAdOverageFee;

  @Column(name = "enable_cpi_tracking")
  private boolean cpiTrackingEnabled;

  @Column(name = "cpi_conversion_notice_url")
  private String cpiConversionNoticeUrl;

  @Column(name = "enable_rtb")
  private boolean rtbEnabled = false;

  @Column(name = "enable_mediation")
  private boolean mediationEnabled = false;

  @Column(name = "rtb_revenue_report_enabled")
  private boolean rtbRevenueReportEnabled = false;

  @Column(name = "salesforce_id")
  @Size(max = 100)
  private String salesforceId;

  @Column(name = "adserving_fee")
  private BigDecimal bidderAdServingFee;

  @Column(name = "status")
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status = Status.ACTIVE;

  @Column(name = "global_alias_name", length = 100)
  private String globalAliasName;

  @Column(name = "disable_ad_feedback", nullable = false)
  private boolean disableAdFeedback = false;

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "company",
      cascade = {CascadeType.ALL})
  @OrderBy("pid DESC")
  @NotAudited
  @JsonIgnore
  private Set<BDRCredit> credits = new LinkedHashSet<>();

  @Column(name = "test")
  private Boolean test = false;

  @Column(name = "selfserve_allowed")
  private boolean selfServeAllowed = false;

  @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL)
  @JsonIgnoreProperties(
      value = {"defaultRtbProfile"},
      allowSetters = true)
  private SellerAttributes sellerAttributes;

  @Column(name = "region_id")
  private Long regionId;

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "publisher",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonIgnore
  private Set<SellerEligibleBidders> eligibleBidders = new HashSet<>();

  @Column(name = "payout_enabled")
  private Boolean payoutEnabled = false;

  @Column(name = "third_party_fraud_detection_enabled", nullable = false)
  private Boolean thirdPartyFraudDetectionEnabled;

  @Column(name = "default_rtb_profiles_enabled", updatable = false)
  private boolean defaultRtbProfilesEnabled;

  @Column(name = "dh_reporting_id", length = 32)
  private String dhReportingId;

  @OptimisticLock(excluded = true)
  @JsonIgnore
  @NotAudited
  @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<BuyerGroup> buyerGroups = new ArrayList<>();

  @Column(name = "default_buyer_group")
  private Long defaultBuyerGroup;

  @OptimisticLock(excluded = true)
  @JsonIgnore
  @NotAudited
  @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<BuyerSeat> buyerSeats = new ArrayList<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "company_attributes",
      joinColumns = @JoinColumn(name = "company_pid", referencedColumnName = "pid"),
      inverseJoinColumns = @JoinColumn(name = "attribute_values_pid", referencedColumnName = "pid"))
  @AuditJoinTable(name = "company_attributes_aud")
  @JsonIgnore
  private Set<InventoryAttributeValue> inventoryAttributeValues = new HashSet<>();

  @Column(nullable = false, length = 3, columnDefinition = "varchar(3) default 'USD'")
  @EqualsAndHashCode.Include
  private String currency = "USD";

  @OptimisticLock(excluded = true)
  @OneToMany(
      mappedBy = "company",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonIgnore
  private Set<HbPartnerCompany> hbPartnerCompany = Sets.newHashSet();

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "company",
      cascade = {CascadeType.ALL})
  @JsonIgnore
  @NotAudited
  private List<SellerMetrics> sellerMetrics;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_seat_id")
  @JsonIgnore
  private SellerSeat sellerSeat;

  @Column(name = "external_ad_verification_enabled", nullable = false)
  private Boolean externalAdVerificationEnabled;

  @Column(name = "fraud_detection_javascript_enabled", nullable = false)
  private Boolean fraudDetectionJavascriptEnabled = true;

  @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonIgnore
  private List<CompanyMdmId> mdmIds = new ArrayList<>();

  @JsonSerialize(using = BigDecimalSerializer.class)
  @Transient
  private BigDecimal credit;

  @Transient
  @Getter(onMethod_ = {@JsonProperty("estimatedTimeRemain")})
  @Setter(onMethod_ = {@JsonIgnore})
  private Company.EstimateTimeRemaining estimatedTimeRemaining;

  @Transient private Integer numberOfRtbTags;

  @Transient private Integer numberOfMediationSites;

  @Transient private Set<String> adsourceNames = new HashSet<>();

  @Transient private Set<String> externalDataProviderNames = new HashSet<>();

  @Transient private Integer activeIOs;

  @Transient private int numberOfUsers = 0;

  @Transient private Boolean hasHeaderBiddingSites;

  /**
   * This is transient because: 1. This field should be in DTO. At the moment entity and DTO for
   * company is mixed up. 2. since it is not needed to have fully filled Seller Seat entity in order
   * to associate this seller with seller seat. Having seller seat pid value is enough.
   */
  @Transient @JsonProperty private Long sellerSeatPid;

  public Company() {
    // Protected allows some protection in terms of new entity creation
    this.thirdPartyFraudDetectionEnabled = true;
  }

  public Company(String name, CompanyType type) {
    this(name, type, null, null);
    this.thirdPartyFraudDetectionEnabled = true;
  }

  public Company(String name, CompanyType type, String url, String description) {
    this.name = name;
    this.type = type;
    this.website = url;
    this.description = description;
    this.rtbEnabled = true;
    this.adServingEnabled = false;
    this.thirdPartyFraudDetectionEnabled = true;
  }

  @PrePersist
  @PreUpdate
  private void prePersistAndUpdate() {
    if (externalAdVerificationEnabled == null) {
      externalAdVerificationEnabled = false;
    }
    if (StringUtils.isBlank(id)) {
      id = new UUIDGenerator().generateUniqueId();
    }
  }

  public void setSellerAttributes(SellerAttributes attributes) {
    if (attributes != null) {
      attributes.setSeller(this);
    }
    this.sellerAttributes = attributes;
  }

  public void setContact(User contact) {
    var previousContact = this.contact;

    this.contact = contact;

    if (contact != null) {
      contact.determinePrimaryContact();
    }
    if (previousContact != null) {
      previousContact.determinePrimaryContact();
    }
  }

  public Long getContactUserPid() {
    if (contactUserPid == null && contact != null) {
      contactUserPid = contact.getPid();
    }
    return contactUserPid;
  }

  public BigDecimal getCredit() {
    if (credit == null && !credits.isEmpty()) {
      credit = BigDecimal.ZERO;
      credits.forEach(bdrCredit -> credit = credit.add(bdrCredit.getAmount()));
    }
    return credit;
  }

  public void addEligibleBidders(SellerEligibleBidders bidder) {
    bidder.setPublisher(this);
    this.eligibleBidders.add(bidder);
  }

  public void setHbPartnerCompany(Set<HbPartnerCompany> hbPartnerCompany) {
    this.hbPartnerCompany.clear();
    this.hbPartnerCompany.addAll(hbPartnerCompany);
  }

  public boolean isAdServingEnabled() {
    return TRUE.equals(adServingEnabled);
  }

  public boolean isRestrictDrillDown() {
    return TRUE.equals(restrictDrillDown);
  }

  public void setThirdPartyFraudDetectionEnabled(Boolean thirdPartyFraudDetectionEnabled) {
    if (thirdPartyFraudDetectionEnabled != null) {
      this.thirdPartyFraudDetectionEnabled = thirdPartyFraudDetectionEnabled;
    }
  }

  @Override
  public String toString() {
    return "Company [name=" + name + ", id=" + id + "]";
  }

  @Getter
  @Setter(onMethod_ = {@JsonIgnore})
  @AllArgsConstructor
  public static class EstimateTimeRemaining implements Serializable {

    private ETR status;
    private BigInteger value;

    public enum ETR {
      NO_SPEND,
      NO_CREDIT,
      VALID
    }
  }
}
