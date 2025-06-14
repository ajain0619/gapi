package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.admin.core.sparta.jpa.model.SiteMetadata;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.admin.core.validator.CheckUniqueEntity;
import com.nexage.admin.core.validator.CheckUniqueGroup;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Where;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "site")
@NamedQuery(
    name = "getTagsWitGivenRtbProfile",
    query = "SELECT t FROM Site s JOIN s.tags t WHERE t.primaryId=:primaryId AND s.status = 1")
@CheckUniqueEntity(
    errorCode = CoreDBErrorCodes.CORE_DB_DUPLICATE_SITE_NAME,
    groups = CheckUniqueGroup.class,
    properties = {"name", "company"})
public class Site implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Joiner JOIN_WITH_COMMA = Joiner.on(",").skipNulls();

  public Site(
      Long pid,
      String name,
      Status status,
      String dcn,
      String id,
      Integer version,
      Long companyPid,
      Boolean metadataEnablement,
      Boolean hbEnabled) {
    this.pid = pid;
    this.name = name;
    this.status = status;
    this.dcn = dcn;
    this.id = id;
    this.version = version;
    this.companyPid = companyPid;
    this.metadataEnablement = metadataEnablement;
    this.hbEnabled = hbEnabled;
  }

  @Column(name = "dcn", nullable = false, updatable = false, unique = true)
  @Size(max = 32)
  @EqualsAndHashCode.Include
  private String dcn;

  @Column(name = "id", nullable = false, updatable = false, unique = true)
  @Size(max = 32)
  private String id;

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @Id
  @EqualsAndHashCode.Include
  private Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  private Integer version;

  @Column(name = "ad_screening", nullable = false)
  @JsonIgnore
  private boolean adScreeningEnabled = false;

  @Column(name = "enable_groups", nullable = false)
  @JsonIgnore
  private boolean groupsEnabled;

  @Column(name = "buyer_timeout")
  private Integer buyerTimeout;

  @Column(name = "consumer_profile_contributed", nullable = false)
  private boolean consumerProfileContributed;

  @Column(name = "consumer_profile_used", nullable = false)
  private boolean consumerProfileUsed;

  @Column(name = "creation_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @JsonIgnore
  private Date creationDate;

  @Column(name = "last_update", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @JsonIgnore
  private Date lastUpdate;

  @Column(name = "days_free")
  private Integer daysFree;

  @Column(name = "revenue_launch_date")
  @Temporal(TemporalType.TIMESTAMP)
  @JsonIgnore
  private Date revenueLaunchDate;

  @Column
  @Size(max = 512)
  private String description;

  @Column(nullable = false)
  @NotNull
  private String domain;

  @Column(name = "ethnicity_map")
  @Size(max = 45)
  private String ethnicityMap;

  @Column(name = "filter_bots", nullable = false)
  private boolean filterBots;

  @Column(name = "gender_map")
  @Size(max = 45)
  private String genderMap;

  @Column(name = "input_date_format")
  @Size(max = 20)
  private String inputDateFormat;

  @Column(nullable = false)
  @NotNull
  @Enumerated(EnumType.STRING)
  private Type type;

  @Column(name = "live", nullable = false)
  private boolean live;

  @Column(name = "marital_status_map")
  @Size(max = 45)
  private String maritalStatusMap;

  @Column(nullable = false)
  @Size(max = 255)
  @NotNull
  @EqualsAndHashCode.Include
  private String name;

  @Column(name = "override_ip", nullable = false)
  private boolean overrideIP;

  @Column(nullable = false)
  @NotNull
  @Enumerated(EnumType.STRING)
  private Platform platform;

  @Column(name = "report_batch_size", nullable = false)
  private Integer reportBatchSize;

  @Column(name = "report_frequency", nullable = false)
  private Integer reportFrequency;

  @Column(name = "rules_update_frequency", nullable = false)
  private Integer rulesUpdateFrequency;

  // TODO: Remove the send_ids field from Site class after deleting its corresponding column from
  // site table in coredb as apart of the SSP Geneva Cleanup Database Dependencies.
  @Column(name = "send_ids")
  private boolean sendIds = true;

  @Column(name = "status", nullable = false)
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status;

  @Column(name = "ad_truth", nullable = false)
  @JsonProperty("isAdTruthEnabled")
  private boolean adTruthEnabled;

  @JsonIgnore @Transient private Integer statusVal;

  @Column(name = "traffic_throttle")
  private Integer trafficThrottle;

  @Column(name = "total_timeout")
  private Integer totalTimeout;

  @Column(nullable = false)
  private String url;

  // TODO: Remove the zip_overlay field from Site class after deleting its corresponding column from
  //  site table in coredb as apart of the SSP Geneva Cleanup Database Dependencies.
  @Column(name = "zip_overlay")
  @JsonIgnore
  private boolean zipOverlayEnabled = true;

  @Column(name = "coppa_restricted", nullable = false)
  private boolean coppaRestricted;

  @Column(name = "rtb1_category_rollup")
  private String rtb1CategoryRollup;

  @Column(name = "global_alias_name")
  private String globalAliasName;

  @Column(name = "metadata_enablement")
  private Boolean metadataEnablement = false;

  @Column(name = "hb_enabled")
  private Boolean hbEnabled = false;
  /**
   * 1. Default fetch type is Eager and setting it to Lazy will eliminate unnecessary queries even
   * for associations not being used. 2. CascadeType is not set to ALL b'se a. company should not be
   * removed if a site is deleted. b. company should not be detached if a site is detached from
   * persistence Context
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  @NotNull
  @JsonIgnore
  private Company company;

  @Column(name = "company_pid", insertable = false, updatable = false)
  private Long companyPid;

  @Transient
  @JsonIgnoreProperties(
      value = {"site"},
      allowSetters = true)
  private SiteDealTerm currentDealTerm;

  @Column(name = "app_bundle", length = 100)
  private String appBundle;

  @Transient private SiteMetadata metadata = new SiteMetadata();

  @Transient private ImpressionGroup impressionGroup;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "default_position",
      joinColumns = @JoinColumn(name = "site_pid", referencedColumnName = "pid"))
  @Column(name = "name")
  private Set<String> defaultPositions = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "impression_group",
      joinColumns = @JoinColumn(name = "site_pid", referencedColumnName = "pid"))
  @Column(name = "name")
  @JsonIgnore
  private Set<String> impressionGroups = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "passthru_parameter",
      joinColumns = @JoinColumn(name = "site_pid", referencedColumnName = "pid"))
  @Column(name = "name")
  private Set<String> passthruParameters = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "iab_cat",
      joinColumns = @JoinColumn(name = "site_pid", referencedColumnName = "pid"))
  @Column(name = "category")
  private Set<String> iabCategories = new HashSet<>();

  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "site",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @OrderBy("pid DESC")
  @JsonIgnore
  private Set<SiteDealTerm> dealTerms = new LinkedHashSet<>();

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "site")
  @Where(clause = "status >= 0")
  @JsonIgnoreProperties(
      value = {"site", "defaultRtbProfile"},
      allowSetters = true)
  private Set<Position> positions = new HashSet<>();

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "site")
  @Where(clause = "status >= 0")
  @AuditMappedBy(mappedBy = "site")
  @JsonIgnoreProperties(
      value = {"site"},
      allowSetters = true)
  private Set<Tag> tags = new HashSet<>();

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "site", orphanRemoval = true)
  @JsonIgnoreProperties(
      value = {"site", "tag", "sellerAttributes"},
      allowSetters = true)
  private Set<RTBProfile> rtbProfiles = new HashSet<>();

  @Column(name = "include_site_name")
  private Integer includeSiteName;

  @Column(name = "site_alias")
  private Long siteAliasId;

  @Column(name = "site_name_alias")
  private String siteNameAlias;

  @Column(name = "include_pub_name")
  private Integer includePubName;

  @Column(name = "pub_alias")
  private Long pubAliasId;

  @Column(name = "pub_name_alias")
  private String pubNameAlias;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "rtb_profile", referencedColumnName = "pid")
  @JsonIgnore
  @JsonBackReference
  private RTBProfile defaultRtbProfile;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "site_attributes",
      joinColumns = @JoinColumn(name = "site_pid", referencedColumnName = "pid"),
      inverseJoinColumns = @JoinColumn(name = "attribute_values_pid", referencedColumnName = "pid"))
  @AuditJoinTable(name = "site_attributes_aud")
  @JsonIgnore
  private Set<InventoryAttributeValue> inventoryAttributeValues = new HashSet<>();

  @OneToMany(
      mappedBy = "site",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonIgnore
  @NotAudited
  private Set<HbPartnerSite> hbPartnerSite = Sets.newHashSet();

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "site",
      cascade = {CascadeType.ALL})
  @JsonIgnore
  @NotAudited
  private Set<SiteMetrics> metrics = new HashSet<>();

  @DecimalMin(value = "0.0", inclusive = false)
  @DecimalMax(value = "100.0")
  @Digits(integer = 3, fraction = 2)
  @Column(name = "creative_success_rate_threshold")
  private BigDecimal creativeSuccessRateThreshold;

  public void addPosition(Position position) {
    positions.add(position);
  }

  public ImpressionGroup getImpressionGroup() {
    if (impressionGroup == null) {
      return new ImpressionGroup(groupsEnabled, impressionGroups);
    }
    return impressionGroup;
  }

  public void setImpressionGroup(ImpressionGroup impGroup) {
    this.impressionGroup = impGroup;
    if (impressionGroup != null) {
      groupsEnabled = impressionGroup.enabled;
      impressionGroups = impressionGroup.groups;
    }
  }

  public void setDescription(String description) {
    this.description = description;
    if (this.description != null && this.description.trim().length() == 0) {
      this.description = null;
    }
  }

  public void setStatus(Status newStatus) {
    this.status = newStatus;
    this.statusVal = newStatus.asInt();
  }

  public Set<String> getDefaultPositions() {
    if (CollectionUtils.isNotEmpty(defaultPositions)) {
      // get site.defaultPosition records, if any, and construct
      // comma delimited defaultPositionNames string in the default position object
      for (Position position : positions) {
        if (position.getIsDefault()) {
          String names = JOIN_WITH_COMMA.join(defaultPositions);
          position.setDefaultPositionNames(names);
          break; // only a single default position
        }
      }
    } else {
      return new HashSet<>();
    }
    return defaultPositions;
  }

  public void addToDealTerms(SiteDealTerm dealTerm) {
    if (this.dealTerms.isEmpty()) {
      this.dealTerms.add(dealTerm);
    } else {
      List<SiteDealTerm> tempList = new ArrayList<>(dealTerms);
      tempList.add(0, dealTerm);
      this.dealTerms.clear();
      this.dealTerms.addAll(tempList);
    }
  }

  public SiteDealTerm getCurrentDealTerm() {
    if (currentDealTerm == null) {
      // sets current deal term for all tags
      // It is efficient this way instead of iterating over the list in each tag
      // To set new value for a Site setter method has to be called additionally
      return getCurrentDealTermWhenNull();
    }
    return currentDealTerm;
  }

  private SiteDealTerm getCurrentDealTermWhenNull() {
    SiteDealTerm result = null;
    Map<Long, SiteDealTerm> tagDealTerms = new HashMap<>();
    for (SiteDealTerm term : getDealTerms()) {
      if (term != null) {
        if (null == term.getTagPid() && result == null) {
          // create via copy constructor as AMF serialization needs this to be a unique object
          result = new SiteDealTerm(term);
        }
        if (null != term.getTagPid() && !tagDealTerms.containsKey(term.getTagPid())) {
          // looking for a tag terms and got it
          // create via copy constructor as AMF serialization needs this to be a unique object
          tagDealTerms.put(term.getTagPid(), new SiteDealTerm(term));
        }
      }
    }
    for (Tag tag : tags) {
      tag.setCurrentDealTerm(tagDealTerms.get(tag.getPid())); // set tag override if exists
    }
    return result;
  }

  public void setHbPartnerSite(Set<HbPartnerSite> hbPartnerSite) {
    this.hbPartnerSite.clear();
    this.hbPartnerSite.addAll(hbPartnerSite);
  }

  @PreUpdate
  public void onUpdate() {
    Date d = new Date();
    lastUpdate = d;
    if (revenueLaunchDate == null && live) {
      revenueLaunchDate = d;
    }
  }

  @PrePersist
  public void onCreate() {
    groupsEnabled = false;
    if (buyerTimeout == null || buyerTimeout == 0) buyerTimeout = 500;
    if (daysFree == null) daysFree = 0;
    if (StringUtils.isBlank(dcn)) dcn = new UUIDGenerator().generateUniqueId();
    id = new UUIDGenerator().generateUniqueId();
    if (reportBatchSize == null || reportBatchSize == 0) reportBatchSize = 10;
    if (reportFrequency == null || reportFrequency == 0) reportFrequency = 180000; // in ms
    if (rulesUpdateFrequency == null || rulesUpdateFrequency == 0)
      rulesUpdateFrequency = 1800000; // in ms
    if (status == null) status = Status.INACTIVE;
    if (totalTimeout == null || totalTimeout == 0) totalTimeout = 5000; // in ms
    if (trafficThrottle == null) trafficThrottle = 0;
    if (platform == null && (type != null && type == Type.MOBILE_WEB)) {
      platform = Platform.OTHER;
    }
    Date d = Calendar.getInstance().getTime();
    creationDate = d;
    lastUpdate = d;
    if (revenueLaunchDate == null) {
      revenueLaunchDate = d;
    }
    if (impressionGroup != null) {
      groupsEnabled = impressionGroup.isEnabled();
      impressionGroups = impressionGroup.getGroups();
    }
    if (StringUtils.isBlank(inputDateFormat)) inputDateFormat = null;

    if (currentDealTerm != null) {
      currentDealTerm.setSite(this);
      currentDealTerm.setEffectiveDate(d);
      addToDealTerms(currentDealTerm);
    }
  }

  public Integer getStatusVal() {
    if (statusVal == null) {
      return status.asInt();
    }
    return statusVal;
  }

  public void setStatusVal(Integer statusVal) {
    if (statusVal != null) {
      this.statusVal = statusVal;
      this.status = Status.fromInt(statusVal);
    }
  }

  public Set<String> getPassthruParameters() {
    return passthruParameters != null ? passthruParameters : new HashSet<>();
  }

  @Override
  public String toString() {
    return (new ReflectionToStringBuilder(this) {
          protected boolean accept(Field f) {
            if (f.getName().equals("company")) {
              return false;
            } else {
              return super.accept(f);
            }
          }
        })
        .toString();
  }

  @Data
  public static final class ImpressionGroup {
    private boolean enabled;
    private Set<String> groups = new HashSet<>();

    @JsonCreator
    public ImpressionGroup(
        @JsonProperty(value = "enabled") boolean enabled,
        @JsonProperty(value = "groups") Set<String> groups) {
      this.enabled = enabled;
      if (groups != null) {
        this.groups.addAll(groups);
      }
    }
  }
}
