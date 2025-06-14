package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.FullScreenTiming;
import com.nexage.admin.core.enums.ImpressionTypeHandling;
import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoResponseProtocol;
import com.nexage.admin.core.enums.VideoSkippable;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.validator.CheckUniqueEntity;
import com.nexage.admin.core.validator.CheckUniqueGroup;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.PositionPlacementCategoryValueConstraint;
import com.nexage.admin.core.validator.UpdateGroup;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Range;

@Entity
@Audited
@Table(name = "position")
@CheckUniqueEntity(
    errorCode = CoreDBErrorCodes.CORE_DB_DUPLICATE_POSITION_NAME,
    groups = CheckUniqueGroup.class,
    properties = {"name", "site"})
@PositionPlacementCategoryValueConstraint(
    anyOf = {
      PlacementCategory.BANNER,
      PlacementCategory.INTERSTITIAL,
      PlacementCategory.MEDIUM_RECTANGLE,
      PlacementCategory.NATIVE_V2,
      PlacementCategory.INSTREAM_VIDEO,
      PlacementCategory.REWARDED_VIDEO,
      PlacementCategory.IN_ARTICLE,
      PlacementCategory.IN_FEED
    },
    groups = {CreateGroup.class, UpdateGroup.class})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Position implements Serializable {

  private static final long serialVersionUID = 8699750994939493170L;

  @Id @GeneratedValue @EqualsAndHashCode.Include private Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  private Integer version;

  @Column(name = "site_pid", insertable = false, updatable = false)
  @NotAudited
  @Setter(AccessLevel.NONE)
  @EqualsAndHashCode.Include
  private Long sitePid;

  @ManyToOne
  @JoinColumn(name = "site_pid", referencedColumnName = "pid")
  @NotNull
  @JsonIgnore
  @JsonBackReference
  private Site site;

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "position")
  @IndexColumn(name = "level")
  // Envers has internal bug to map collections asper the given indexColumn
  @AuditMappedBy(mappedBy = "position")
  @MapKey(name = "level")
  // Thus this line gives envers an explicit mapping key to initialize the collection
  @JsonIgnoreProperties(
      value = {"position"},
      allowSetters = true)
  private List<Tier> tiers = new ArrayList<>();

  @NotNull
  @Column(nullable = false)
  @Size(max = 45)
  @EqualsAndHashCode.Include
  private String name;

  @Size(max = 200)
  @Column
  private String memo;

  @Column(name = "is_default")
  private Boolean isDefault;

  @Column(name = "is_interstitial")
  private Boolean isInterstitial;

  @Column(name = "mraid_support")
  private MRAIDSupport mraidSupport;

  @Column(name = "video_support")
  @Enumerated(EnumType.ORDINAL)
  private VideoSupport videoSupport;

  @Column(name = "screen_location")
  @Type(type = "com.nexage.admin.core.usertype.ScreenLocationUserType")
  private ScreenLocation screenLocation;

  @Column(name = "mraid_adv_tracking", nullable = false)
  private boolean mraidAdvancedTracking = true;

  @Column(name = "ad_size")
  @Size(max = 500)
  private String adSize;

  @Column(name = "static_ad_unit")
  private Boolean staticAdUnit;

  @Column(name = "rich_media_ad_unit")
  private Boolean richMediaAdUnit;

  @Column(name = "rm_mraid_version")
  private Boolean richMediaMRAIDVersion;

  @Column(name = "video_mraid_2")
  private Boolean videoMRAID2;

  @Column(name = "video_proprietary")
  private Boolean videoProprietary;

  @Column(name = "video_vast", nullable = true)
  private Boolean videoVast;

  @Column(name = "video_response_protocol", nullable = true)
  @Type(type = "com.nexage.admin.core.usertype.VideoResponseProtocolUserType")
  private VideoResponseProtocol videoResponseProtocol;

  @Column(name = "video_playback_method", nullable = true)
  @Size(max = 25)
  private String videoPlaybackMethod;

  @Column(name = "video_linearity")
  @Type(type = "com.nexage.admin.core.usertype.VideoResponseLinearity")
  private VideoLinearity videoLinearity;

  @Column(name = "video_start_delay", nullable = true)
  private Integer videoStartDelay;

  @Column(name = "video_maxdur", nullable = true)
  @Range(min = 1, max = 999)
  private Integer videoMaxdur;

  @Column(name = "fullscreen_timing")
  @Type(type = "com.nexage.admin.core.usertype.FullScreenTimingUserType")
  private FullScreenTiming fullScreenTiming;

  @Column(name = "position_alias_name", unique = true)
  @Size(max = 45)
  private String positionAliasName;

  @Column(name = "height")
  @Range(min = 1, max = Short.MAX_VALUE)
  private Integer height;

  @Column(name = "width")
  @Range(min = 1, max = Short.MAX_VALUE)
  private Integer width;

  @Column(name = "video_skippable", nullable = true)
  @Enumerated(EnumType.ORDINAL)
  private VideoSkippable videoSkippable;

  @Column(name = "video_skipthreshold", nullable = true)
  @Range(min = 0, max = Short.MAX_VALUE)
  private Integer videoSkipThreshold;

  @Column(name = "video_skipoffset", nullable = true)
  @Range(min = 0, max = 999)
  private Integer videoSkipOffset;

  @NotNull
  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status = Status.ACTIVE;

  @Column(name = "placement_type")
  @Enumerated(EnumType.ORDINAL)
  private PlacementCategory placementCategory;

  @Column(name = "traffic_type")
  @Enumerated(EnumType.ORDINAL)
  private TrafficType trafficType = TrafficType.MEDIATION;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "rtb_profile", referencedColumnName = "pid")
  @JsonIgnore
  @JsonBackReference
  private RTBProfile defaultRtbProfile;

  @Transient @JsonIgnore // TODO cleanup after geneva migration
  // this comma separated list of default position names will be persisted thru the
  // siteDTO.defaultPositions collection
  // this is just a convenience for the client to add send them back and forth from the position
  // page
  private String defaultPositionNames;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @JsonIgnore
  private Date updatedOn;

  @OneToMany(
      mappedBy = "position",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonIgnore
  @NotAudited
  private final Set<HbPartnerPosition> hbPartnerPosition = Sets.newHashSet();

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "position",
      cascade = {CascadeType.ALL})
  @JsonIgnore
  @NotAudited
  private final Set<PositionMetrics> metrics = new HashSet<>();

  @Column(name = "ad_size_type")
  @Enumerated(EnumType.ORDINAL)
  private AdSizeType adSizeType;

  @Column(name = "native_config")
  @Size(max = 32767)
  private String nativeConfig;

  @OneToOne(
      cascade = CascadeType.ALL,
      mappedBy = "position",
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private PlacementDooh placementDooh;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "position")
  private PositionBuyer positionBuyer;

  @Column(name = "external_ad_verification_sampling_rate")
  private Float externalAdVerificationSamplingRate;

  @DecimalMin(value = "0.0", inclusive = false)
  @DecimalMax(value = "100.0")
  @Digits(integer = 3, fraction = 2)
  @Column(name = "creative_success_rate_threshold")
  @Getter
  @Setter
  private BigDecimal creativeSuccessRateThreshold;

  @NotNull
  @ColumnDefault("0")
  @Column(name = "impression_type_handling", nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private ImpressionTypeHandling impressionTypeHandling =
      ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG;

  public List<Tier> getTiers() {
    return tiers;
  }

  public void setTiers(List<Tier> tiers) {
    this.tiers.clear();
    this.tiers.addAll(tiers);
  }

  public void setMetrics(Set<PositionMetrics> metrics) {
    this.metrics.clear();
    if (metrics != null) {
      this.metrics.addAll(metrics);
    }
  }

  @Transient private Boolean longform = false;

  public Boolean getLongform() {
    return longform;
  }

  public void setLongform(Boolean longform) {
    this.longform = longform;
  }

  @PreUpdate
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }

  public void renumberTiers() {
    var level = 0;
    for (Tier tier : tiers) {
      tier.setLevel(level++);
    }
  }

  public Tier newTier() {
    var tier = new Tier(tiers.size());
    tiers.add(tier);
    return tier;
  }

  public void removeTier(Tier tier) {
    tiers.remove(tier);
    renumberTiers();
  }

  public Tier getTier(int level) {
    Tier tier = null;
    for (Tier t : tiers) {
      if (level == t.getLevel()) {
        tier = t;
        break;
      }
    }
    return tier;
  }

  public void removeVideo() {
    this.videoLinearity = null;
    this.videoMaxdur = null;
    this.videoPlaybackMethod = null;
    this.videoStartDelay = null;
    this.videoSkippable = null;
    this.videoSkipThreshold = null;
    this.videoSkipOffset = null;
  }

  public void setVideoSupport(VideoSupport videoSupport) {
    this.videoSupport = videoSupport;
  }

  public VideoLinearity getVideoLinearity() {
    if (videoLinearity == null
        && (videoSupport == VideoSupport.VIDEO || videoSupport == VideoSupport.VIDEO_AND_BANNER)) {
      return VideoLinearity.LINEAR;
    }
    return videoLinearity;
  }

  public void setHbPartnerPosition(Set<HbPartnerPosition> hbPartnerPosition) {
    this.hbPartnerPosition.clear();
    if (hbPartnerPosition != null) {
      this.hbPartnerPosition.addAll(hbPartnerPosition);
    }
  }

  public void setPlacementDooh(PlacementDooh placementDooh) {
    this.placementDooh = placementDooh;
    if (this.placementDooh != null) {
      this.placementDooh.setPosition(this);
      this.placementDooh.setPid(this.getPid());
    }
  }

  public void setImpressionTypeHandling(ImpressionTypeHandling impressionTypeHandling) {
    this.impressionTypeHandling = impressionTypeHandling;
  }

  public ImpressionTypeHandling getImpressionTypeHandling() {
    return impressionTypeHandling;
  }

  @PrePersist
  public void onCreate() {
    if (isDefault == null) isDefault = false;
    if (isInterstitial == null) isInterstitial = false;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
