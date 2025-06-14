package com.nexage.admin.core.bidder.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.bidder.type.BDRMraidCompliance;
import com.nexage.admin.core.bidder.type.BDRStatus;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.envers.Audited;

/**
 * @author Eugeny Yurko
 * @since 21.10.2014
 */
@Entity
@Audited
@Table(name = "bdr_targetgroup_creative")
public class BDRTargetGroupCreative implements HasCreativeWeight {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonBackReference
  @JoinColumn(name = "targetgroup_pid", referencedColumnName = "pid")
  private BdrTargetGroup targetGroup;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
  @JoinColumn(name = "creative_pid", referencedColumnName = "pid")
  @JsonBackReference
  private BdrCreative creative;

  @Column(name = "weight")
  private double weight;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @JsonIgnore
  private Date updatedOn;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  public BdrTargetGroup getTargetGroup() {
    return targetGroup;
  }

  public void setTargetGroup(BdrTargetGroup targetGroup) {
    this.targetGroup = targetGroup;
  }

  public BdrCreative getCreative() {
    return creative;
  }

  public void setCreative(BdrCreative creative) {
    this.creative = creative;
  }

  @Override
  public Long getCreativePid() {
    return creative.getPid();
  }

  @Override
  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  @PrePersist
  @PreUpdate
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }

  public Date getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(Date updatedOn) {
    this.updatedOn = updatedOn;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Long getPid() {
    return pid;
  }

  /*
   * AUDIT support
   * As creatives are not updated when they are associated or unassociated with a target group
   * the targetgroupCreative audit record could reference a pid in the bdr_creative_aud table that does
   * not exist. This inner class works around that issue.
   *
   */
  @SuppressWarnings("unused")
  private static class CreativeInfo {
    private BdrCreative creative;

    CreativeInfo(BdrCreative creative) {
      this.creative = creative;
    }

    public Long getPid() {
      return creative.getPid();
    }

    public String getName() {
      return creative.getName();
    }

    public BDRStatus getStatus() {
      return creative.getStatus();
    }

    public String getBannerURL() {
      return creative.getBannerURL();
    }

    public Integer getWidth() {
      return creative.getWidth();
    }

    public Integer getHeight() {
      return creative.getHeight();
    }

    public String getCustomMarkup() {
      return creative.getCustomMarkup();
    }

    public String getLandingURL() {
      return creative.getLandingURL();
    }

    public String getTrackingURL() {
      return creative.getTrackingURL();
    }

    public String getIndicativeURL() {
      return creative.getIndicativeURL();
    }

    public String getExternalId() {
      return creative.getExternalId();
    }

    public String getNexageBannerUrl() {
      return creative.getNexageBannerUrl();
    }

    public BDRMraidCompliance getMraidCompliance() {
      return creative.getMraidCompliance();
    }
  }

  @Transient
  @JsonIgnore
  public CreativeInfo getCreativeDetail() {
    return (creative == null) ? null : new CreativeInfo(creative);
  }

  @Override
  public String toString() {
    return "BDRTargetGroupCreative [pid="
        + pid
        + ", targetGroup="
        + targetGroup
        + ", creative="
        + creative
        + ", weight="
        + weight
        + "]";
  }
}
