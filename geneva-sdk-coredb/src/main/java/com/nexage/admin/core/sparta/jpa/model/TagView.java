package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSkippable;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Tag;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "tag")
@Immutable
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class TagView implements Serializable {

  private static final long serialVersionUID = 3830780056076164037L;

  @EqualsAndHashCode.Include @ToString.Include @Id private Long pid;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "name")
  private String name;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "id")
  private String identifier;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "site_pid")
  private Long sitePid;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "buyer_pid")
  private Long buyerPid;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status;

  @Column(name = "primary_id")
  private String primaryId;

  @Column(name = "primary_name")
  private String primaryName;

  @Column(name = "secondary_id")
  private String secondaryId;

  @Column(name = "secondary_name")
  private String secondaryName;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "ecpm_provision")
  private String ecpmProvision;

  @Column(name = "ecpm_auto")
  private Double ecpmAuto;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "ecpm_manual")
  private Double ecpmManual;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Formula("(SELECT rtb.pub_net_reserve FROM exchange_site_tag rtb WHERE rtb.tag_id = primary_id)")
  private Double rtbFloor;

  @Formula("(SELECT rtb.description FROM exchange_site_tag rtb WHERE rtb.tag_id = primary_id)")
  private String rtbDescription;

  @Formula("(SELECT if(count(1) = 0, 0, 1) FROM exchange_site_tag rtb WHERE rtb.tag_id=primary_id)")
  private boolean exchangeTag;

  @Column
  @Enumerated(EnumType.ORDINAL)
  private Tag.Owner owner;

  @Column(name = "url_template")
  private String urlTemplate;

  @Column(name = "buyer_class")
  private String buyerClass;

  @Column(name = "get_template")
  private String getTemplate;

  @Column(name = "post_template")
  private String postTemplate;

  @Column(name = "additional_get")
  private String additionalGet;

  @Column(name = "additional_post")
  private String additionalPost;

  @Column(name = "clickthrough_disable")
  private Boolean clickthroughDisable;

  @Column(name = "noad_regex")
  private String noAdRegex;

  @Column(name = "adspaceid_template")
  private String adSpaceIdTemplate;

  @Column(name = "adspacename_template")
  private String adSpaceNameTemplate;

  @Column(name = "postprocess_template")
  private String postProcessTemplate;

  @Column(name = "httpheader_template")
  private String httpHeaderTemplate;

  @Column(name = "adnetreport_username")
  private String adNetReportUserName;

  @Column(name = "adnetreport_password")
  private String adNetReportPassword;

  @Transient private SiteDealTerm currentDealTerm;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "tag", orphanRemoval = true)
  private Set<TagRule> rules = new HashSet<>();

  @Formula("(SELECT a.name FROM ad_source a WHERE a.pid = buyer_pid)")
  private String buyerName;

  @Formula("(SELECT a.logo FROM ad_source a WHERE a.pid = buyer_pid)")
  private String buyerLogo;

  @Column(name = "monetization")
  private Boolean monetization;

  @Column(name = "ad_size")
  private String adSize;

  @Column(name = "return_raw_response")
  private Boolean returnRawResponse;

  @Column(name = "import_revenue_flag")
  private Boolean importRevenueFlag;

  @Column(name = "is_interstitial")
  private Boolean isInterstitial;

  @Column(name = "is_video_allowed")
  private Boolean isVideoAllowed;

  @ManyToOne(fetch = FetchType.EAGER)
  private TagPosition position;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private Date updatedOn;

  @Column(name = "video_support")
  @Enumerated(EnumType.ORDINAL)
  private VideoSupport videoSupport;

  @Column(name = "video_playback_method", nullable = true)
  private String videoPlaybackMethod;

  @Column(name = "video_maxdur", nullable = true)
  private Integer videoMaxdur;

  @Column(name = "video_start_delay", nullable = true)
  private Integer videoStartDelay;

  @Column(name = "video_linearity")
  @Type(type = "com.nexage.admin.core.usertype.VideoResponseLinearity")
  private VideoLinearity videoLinearity;

  @Column(name = "video_skipthreshold", nullable = true)
  private Integer videoSkipThreshold;

  @Column(name = "video_skippable", nullable = true)
  @Enumerated(EnumType.ORDINAL)
  private VideoSkippable videoSkippable;

  @Column(name = "video_skipoffset", nullable = true)
  private Integer videoSkipOffset;

  @Column(name = "screen_location")
  @Type(type = "com.nexage.admin.core.usertype.ScreenLocationUserType")
  private ScreenLocation screenLocation;

  @Column(name = "width")
  private Integer width;

  @Column(name = "height")
  private Integer height;

  @Column(name = "autogenerated")
  private boolean isAutogenerated;

  @Column(name = "adnetreport_apitoken")
  private String apiToken;

  @Column(name = "adnetreport_apikey")
  private String apiKey;

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "tag")
  private RTBProfile rtbProfile;

  @Transient private Long tierPid;
  @Transient private TierType tierType;
  @Transient private Integer tierLevel;
  @Transient private Boolean belongsToRTBGroup;
  /** @deprecated use the inclusive term {@link #filterBiddersAllowlist} instead. */
  @Deprecated(since = "SSP-22461", forRemoval = true)
  @Transient
  private Boolean filterBiddersWhitelist;

  @Transient private Boolean filterBiddersAllowlist;
  @Transient private Boolean useDefaultBidders;
  @Transient private Boolean useDefaultBlock;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "tag")
  private TagController tagController;

  @Column(name = "VERSION", nullable = false)
  private Integer version;

  /** @deprecated use the inclusive term {@link #filterBiddersAllowlist} instead. */
  @Deprecated(since = "SSP-22461", forRemoval = true)
  public void setFilterBiddersWhitelist(Boolean filterBiddersWhitelist) {
    if (this.filterBiddersAllowlist != null) {
      this.filterBiddersWhitelist = this.filterBiddersAllowlist;
    } else if (filterBiddersWhitelist != null) {
      this.filterBiddersAllowlist = filterBiddersWhitelist;
      this.filterBiddersWhitelist = filterBiddersWhitelist;
    }
  }

  /** @deprecated use the inclusive term {@link #filterBiddersAllowlist} instead. */
  @Deprecated(since = "SSP-22461", forRemoval = true)
  public Boolean getFilterBiddersWhitelist() {
    return filterBiddersAllowlist != null ? filterBiddersAllowlist : filterBiddersWhitelist;
  }

  public void setFilterBiddersAllowlist(Boolean filterBiddersAllowlist) {
    // To give precedence to allowlist over whitelist
    if (filterBiddersAllowlist != null
        && (this.filterBiddersAllowlist == null
            || this.filterBiddersWhitelist == null
            || !this.filterBiddersAllowlist.equals(filterBiddersAllowlist))) {
      this.filterBiddersAllowlist = filterBiddersAllowlist;
      this.filterBiddersWhitelist = filterBiddersAllowlist;
    }
  }

  public Boolean getFilterBiddersAllowlist() {
    return filterBiddersAllowlist != null ? filterBiddersAllowlist : filterBiddersWhitelist;
  }
}
