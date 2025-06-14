package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSkippable;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.admin.core.sparta.jpa.model.TagController;
import com.nexage.admin.core.sparta.jpa.model.TagPosition;
import com.nexage.admin.core.sparta.jpa.model.TagRule;
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
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Audited
@Table(name = "tag")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Tag implements Serializable, Cloneable {

  private static final long serialVersionUID = -1448273681035430887L;

  public static final String QUERY_GET_TAG_HIERARCHY = "getTagHierarchy";
  private static final String DEFAULT_BUYER_NAME = "buyerName";

  @GeneratedValue(generator = "UseIdOrGenerate", strategy = GenerationType.AUTO)
  @GenericGenerator(
      name = "UseIdOrGenerate",
      strategy = "com.nexage.admin.core.sparta.jpa.model.UseIdOrGenerate")
  @Column(nullable = false, updatable = false)
  @Id
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  private Integer version;

  @Column
  @Size(max = 255)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @Column(name = "id")
  @Size(max = 32)
  @JsonProperty("id")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String identifier;

  @Column(name = "site_pid", insertable = false, updatable = false)
  @NotAudited
  private Long sitePid;

  @ManyToOne
  @JoinColumn(name = "site_pid", referencedColumnName = "pid")
  @JsonIgnore
  @JsonBackReference
  private Site site;

  @Column(name = "buyer_pid")
  private Long buyerPid;

  @NonNull
  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status;

  @Column(name = "primary_id")
  @Size(max = 100)
  private String primaryId;

  @Column(name = "primary_name")
  @Size(max = 255)
  private String primaryName;

  @Column(name = "secondary_id")
  @Size(max = 100)
  private String secondaryId;

  @Column(name = "secondary_name")
  @Size(max = 255)
  private String secondaryName;

  @Column(name = "ecpm_provision")
  private String ecpmProvision;

  @Column(name = "ecpm_auto")
  private Double ecpmAuto;

  @Column(name = "ecpm_manual")
  private Double ecpmManual;

  @Formula("(SELECT rtb.pub_net_reserve FROM exchange_site_tag rtb WHERE rtb.tag_id = primary_id)")
  @NotAudited
  private Double rtbFloor;

  @Formula("(SELECT rtb.description FROM exchange_site_tag rtb WHERE rtb.tag_id = primary_id)")
  @NotAudited
  private String rtbDescription;

  @JsonIgnore
  @Formula("(SELECT if(count(1) = 0, 0, 1) FROM exchange_site_tag rtb WHERE rtb.tag_id=primary_id)")
  @NotAudited
  private boolean exchangeTag;

  public enum Owner {
    Publisher,
    Nexage
  }

  @Column
  @Enumerated(EnumType.ORDINAL)
  private Owner owner;

  @Column(name = "buyer_class")
  private String buyerClass;

  @Column(name = "url_template")
  private String urlTemplate;

  @Column(name = "post_template")
  private String postTemplate;

  @Column(name = "get_template")
  private String getTemplate;

  @Column(name = "additional_post")
  private String additionalPost;

  @Column(name = "additional_get")
  private String additionalGet;

  @Column(name = "noad_regex")
  @JsonProperty("noadRegex")
  private String noAdRegex;

  @Column(name = "clickthrough_disable")
  private Boolean clickthroughDisable;

  @Column(name = "adspaceid_template")
  @Size(max = 255)
  private String adSpaceIdTemplate;

  @Column(name = "adspacename_template")
  @Size(max = 255)
  private String adSpaceNameTemplate;

  @Column(name = "postprocess_template")
  private String postProcessTemplate;

  @Column(name = "httpheader_template")
  private String httpHeaderTemplate;

  @Column(name = "adnetreport_username")
  @Size(max = 255)
  private String adNetReportUserName;

  @Column(name = "adnetreport_password")
  @Size(max = 255)
  private String adNetReportPassword;

  @JsonIgnoreProperties(
      value = {"site"},
      allowSetters = true)
  @Transient
  private SiteDealTerm currentDealTerm;

  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "tag",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonIgnoreProperties(
      value = {"tag"},
      allowSetters = true)
  private Set<TagRule> rules = new HashSet<>();

  @Formula("(SELECT a.name FROM ad_source a WHERE a.pid = buyer_pid)")
  @NotAudited
  private String buyerName;

  @Formula("(SELECT a.logo FROM ad_source a WHERE a.pid = buyer_pid)")
  @NotAudited
  private String buyerLogo;

  @Column private Boolean monetization;

  @Column(name = "return_raw_response")
  private Boolean returnRawResponse;

  @Column(name = "is_interstitial")
  private Boolean isInterstitial;

  @Column(name = "ad_size")
  private String adSize;

  @Column(name = "import_revenue_flag")
  private Boolean importRevenueFlag;

  @Column(name = "is_video_allowed")
  private Boolean isVideoAllowed;

  @ManyToOne(fetch = FetchType.EAGER)
  private TagPosition position;

  @Column(name = "position_pid", insertable = false, updatable = false)
  private Long positionPid;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @JsonIgnore
  private Date updatedOn;

  @Column(name = "video_support")
  @Enumerated(EnumType.ORDINAL)
  private VideoSupport videoSupport;

  @Column(name = "screen_location")
  @Type(type = "com.nexage.admin.core.usertype.ScreenLocationUserType")
  private ScreenLocation screenLocation;

  @Column(name = "video_playback_method", nullable = true)
  private String videoPlaybackMethod;

  @Column(name = "video_linearity")
  @Type(type = "com.nexage.admin.core.usertype.VideoResponseLinearity")
  private VideoLinearity videoLinearity;

  @Column(name = "video_start_delay", nullable = true)
  private Integer videoStartDelay;

  @Column(name = "video_maxdur", nullable = true)
  private Integer videoMaxdur;

  @Column(name = "height")
  private Integer height;

  @Column(name = "width")
  private Integer width;

  @Column(name = "video_skippable", nullable = true)
  @Enumerated(EnumType.ORDINAL)
  private VideoSkippable videoSkippable;

  @Column(name = "video_skipthreshold", nullable = true)
  private Integer videoSkipThreshold;

  @Column(name = "video_skipoffset", nullable = true)
  private Integer videoSkipOffset;

  @Column(name = "adnetreport_apikey")
  private String apiKey;

  @Column(name = "adnetreport_apitoken")
  private String apiToken;

  @Column(name = "autogenerated")
  private boolean isAutogenerated;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "tag")
  @AuditMappedBy(mappedBy = "tag")
  @JsonIgnore
  private RTBProfile rtbProfile;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "tag")
  @AuditMappedBy(mappedBy = "tag")
  @JsonIgnore
  private TagController tagController;

  @Transient private List<Deployment> deployments = new ArrayList<>();

  public String getBuyerName() {
    return StringUtils.isNotBlank(buyerName) ? buyerName : DEFAULT_BUYER_NAME;
  }

  @JsonIgnore
  @Transient
  public boolean isPublisherSelfServeTag() {
    return position != null;
  }

  public void setAdditionalPost(String additionalPost) {
    this.additionalPost = trimmed(additionalPost);
  }

  public void setAdditionalGet(String additionalGet) {
    this.additionalGet = trimmed(additionalGet);
  }

  @JsonIgnore
  public BigDecimal getNexageRevenueShareOverride() {
    return null != getCurrentDealTerm() ? getCurrentDealTerm().getNexageRevenueShare() : null;
  }

  @JsonIgnore
  public BigDecimal getRtbFeeOverride() {
    return null != getCurrentDealTerm() ? getCurrentDealTerm().getRtbFee() : null;
  }

  public void addToDeployments(Deployment deployment) {
    deployments.add(deployment);
  }

  public Tag clone() throws CloneNotSupportedException {
    Tag cloned = (Tag) super.clone();
    cloned.setDeployments(new ArrayList<>(this.getDeployments()));
    cloned.setRules(new HashSet<>(this.getRules()));
    return cloned;
  }

  private String trimmed(String input) {
    return input != null ? input.trim() : null;
  }

  @PreUpdate
  private void preUpdate() {
    updatedOn = Calendar.getInstance().getTime();
  }

  @PrePersist
  public void prePersist() {
    if (monetization == null) {
      monetization = true;
    }
  }

  @Getter
  @Setter
  public static final class Deployment implements Serializable {

    public static final long serialVersionUID = 42L;

    private String position;
    private int tier;
  }
}
