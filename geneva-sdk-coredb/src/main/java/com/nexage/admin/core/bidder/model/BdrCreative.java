package com.nexage.admin.core.bidder.model;

import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import com.nexage.admin.core.bidder.type.BDRMraidCompliance;
import com.nexage.admin.core.bidder.type.BDRStatus;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Data
@EqualsAndHashCode
@Entity
@Audited
@Table(name = "bdr_creative")
public class BdrCreative {

  @Setter(AccessLevel.NONE)
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "ext_id", length = 32, unique = true)
  private String externalId;

  @EqualsAndHashCode.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "advertiser_pid", referencedColumnName = "pid", updatable = false)
  @JsonIgnore
  @JsonBackReference
  private BDRAdvertiser advertiser;

  // Read only for sending it to client
  @Column(name = "advertiser_pid", insertable = false, updatable = false)
  @NotAudited
  private Long advertiserPid;

  @Column(name = "name", length = 100)
  private String name;

  @Column(name = "status", updatable = false)
  @Type(type = "com.nexage.admin.core.bidder.usertype.BDRStatusUserType")
  private BDRStatus status = BDRStatus.ACTIVE;

  @Column(name = "banner", length = 1024)
  private String bannerURL;

  @Column(name = "width")
  private Integer width;

  @Column(name = "height")
  private Integer height;

  @Column(name = "custom_markup", length = 65535)
  private String customMarkup;

  @Column(name = "landing_url", length = 4096)
  private String landingURL;

  @Column(name = "tracking_url", length = 4096)
  private String trackingURL;

  @Column(name = "indicative_url", length = 4096)
  private String indicativeURL;

  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "creative", fetch = FetchType.EAGER)
  @JsonBackReference
  @JsonIgnore
  @NotAudited // association/disassociation mustn't trigger audit events.
  private Set<BDRTargetGroupCreative> targetGroupCreatives = Sets.newHashSet();

  @Column(name = "mraid_compliance")
  @Type(type = "com.nexage.admin.core.bidder.usertype.BDRMraidComplianceUserType")
  private BDRMraidCompliance mraidCompliance = BDRMraidCompliance.NONE;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @JsonIgnore
  private Date updatedOn;

  @Transient private String nexageBannerUrl;

  @PrePersist
  @PreUpdate
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }

  public void setNexageBannerUrl(String host) {
    if (host != null && bannerURL != null) {
      nexageBannerUrl = new StringBuilder(host).append(bannerURL).toString();
    }
  }

  public Set<BdrTargetGroup> getTargetGroups() {
    return targetGroupCreatives.stream()
        .map(BDRTargetGroupCreative::getTargetGroup)
        .collect(toSet());
  }
}
