package com.nexage.admin.core.bidder.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nexage.admin.core.enums.Status;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "bdr_targetgroup")
public class BdrTargetGroup {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lineitem_pid")
  @JsonBackReference
  private BDRLineItem lineItem;

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      mappedBy = "targetGroup",
      orphanRemoval = true)
  @AuditMappedBy(mappedBy = "targetGroup")
  @JsonManagedReference
  private Set<BDRTarget> targets = new HashSet<>();

  /*    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
  @JoinTable(name = "bdr_targetgroup_creative", joinColumns = {@JoinColumn(name = "targetgroup_pid", referencedColumnName = "pid")}, inverseJoinColumns = {@JoinColumn(name = "creative_pid", referencedColumnName = "pid")})
  @AuditJoinTable(name = "bdr_targetgroup_creative_aud", inverseJoinColumns = {@JoinColumn(name = "creative_pid", referencedColumnName = "pid")})
  @JsonIgnore*/
  @Transient @JsonIgnore private Set<BdrCreative> creatives = new HashSet<>();

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "targetGroup")
  @AuditJoinTable(
      name = "bdr_targetgroup_creative_aud",
      inverseJoinColumns = {@JoinColumn(name = "creative_pid", referencedColumnName = "pid")})
  @JsonIgnore
  private Set<BDRTargetGroupCreative> targetGroupCreatives = new HashSet<>();

  @Column(name = "max_bid")
  private BigDecimal maxPrice;

  @Column(name = "cap_daily_imp")
  private Long dailyImpressionCap;

  @Column(name = "cap_daily_spend")
  private BigDecimal dailySpendCap;

  @Column(name = "name")
  private String name;

  @Column(name = "status")
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status = Status.ACTIVE;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @JsonIgnore
  private Date updatedOn;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @PrePersist
  @PreUpdate
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }

  public BDRLineItem getLineItem() {
    return lineItem;
  }

  public void setLineItem(BDRLineItem lineItem) {
    this.lineItem = lineItem;
  }

  public Long getPid() {
    return pid;
  }

  public void addToTargets(BDRTarget target) {
    targets.add(target);
    target.setTargetGroup(this);
  }

  public Set<BDRTarget> getTargets() {
    return targets;
  }

  public void setTargets(Set<BDRTarget> targets) {
    this.targets = targets;
  }

  public Set<BdrCreative> getCreatives() {
    return creatives;
  }

  public void setCreatives(Set<BdrCreative> creatives) {
    this.creatives = creatives;
  }

  public Set<BDRTargetGroupCreative> getTargetGroupCreatives() {
    return targetGroupCreatives;
  }

  public void setTargetGroupCreatives(Set<BDRTargetGroupCreative> targetGroupCreatives) {
    this.targetGroupCreatives = targetGroupCreatives;
  }

  public void addToCreatives(BdrCreative creative) {
    creatives.add(creative);
  }

  public void addToTargetGroupCreatives(BDRTargetGroupCreative targetGroupCreative) {
    targetGroupCreatives.add(targetGroupCreative);
  }

  public BigDecimal getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(BigDecimal maxPrice) {
    this.maxPrice = maxPrice;
  }

  public Long getDailyImpressionCap() {
    return dailyImpressionCap;
  }

  public void setDailyImpressionCap(Long dailyImpressionCap) {
    this.dailyImpressionCap = dailyImpressionCap;
  }

  public BigDecimal getDailySpendCap() {
    return dailySpendCap;
  }

  public void setDailySpendCap(BigDecimal dailySpendCap) {
    this.dailySpendCap = dailySpendCap;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Transient
  public Long getLineitemPid() {
    return lineItem != null ? lineItem.getPid() : null;
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

  @Override
  public int hashCode() {
    final var prime = 31;
    var result = 1;
    result = prime * result + ((lineItem == null) ? 0 : lineItem.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((pid == null) ? 0 : pid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    else if (obj != null && getClass() == obj.getClass()) {
      BdrTargetGroup other = (BdrTargetGroup) obj;
      return Objects.equals(pid, other.pid)
          && Objects.equals(name, other.name)
          && Objects.equals(lineItem, other.lineItem);
    }
    return false;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public static class Cloner {

    private final BdrTargetGroup targetGroup;
    private BDRLineItem lineItem;
    private boolean copyCreatives;

    public Cloner(BdrTargetGroup targetGroup) {
      this.targetGroup = targetGroup;
    }

    public Cloner lineItem(BDRLineItem lineItem) {
      this.lineItem = lineItem;
      return this;
    }

    public Cloner copyCreatives(boolean copyCreatives) {
      this.copyCreatives = copyCreatives;
      return this;
    }

    public BdrTargetGroup build() {
      var clone = new BdrTargetGroup();
      clone.setCreatives(null);
      clone.setTargetGroupCreatives(null);
      clone.setDailyImpressionCap(targetGroup.getDailyImpressionCap());
      clone.setDailySpendCap(targetGroup.getDailySpendCap());
      clone.setMaxPrice(targetGroup.getMaxPrice());
      clone.setName("Clone of " + targetGroup.getName());
      clone.setStatus(targetGroup.getStatus());
      clone.setLineItem(lineItem);

      if (targetGroup.getTargets() != null) {
        if (clone.getTargets() == null) clone.setTargets(new HashSet<>());
        for (BDRTarget target : targetGroup.getTargets()) {
          BDRTarget cloneTarget = new BDRTarget.Cloner(target).targetGroup(clone).build();
          clone.addToTargets(cloneTarget);
        }
      }

      if (targetGroup.getTargetGroupCreatives() != null && copyCreatives) {
        if (clone.getTargetGroupCreatives() == null) clone.setTargetGroupCreatives(new HashSet<>());
        for (BDRTargetGroupCreative targetGroupCreative : targetGroup.getTargetGroupCreatives()) {
          clone.addToTargetGroupCreatives(targetGroupCreative);
        }
      }
      return clone;
    }
  }
}
