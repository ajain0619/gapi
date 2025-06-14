package com.nexage.admin.core.bidder.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.bidder.type.BDRRule;
import com.nexage.admin.core.bidder.type.BDRTargetType;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "bdr_target")
public class BDRTarget {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "target_type")
  @Enumerated(EnumType.ORDINAL)
  private BDRTargetType targetType;

  @Column(name = "rule")
  @Enumerated(EnumType.ORDINAL)
  private BDRRule rule = BDRRule.ANYOF;

  @Column(name = "data", length = 65535, nullable = false)
  private String data;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonBackReference
  private BdrTargetGroup targetGroup;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on", nullable = false)
  @JsonIgnore
  private Date updatedOn;

  public Long getPid() {
    return pid;
  }

  @Transient
  public Long getTargetGroupPid() {
    return targetGroup != null ? targetGroup.getPid() : null;
  }

  @Transient
  public void setTargetGroupPid(Long targetGroupPid) {}

  public BDRTargetType getTargetType() {
    return targetType;
  }

  public void setTargetType(BDRTargetType targetType) {
    this.targetType = targetType;
  }

  public BDRRule getRule() {
    return rule;
  }

  public void setRule(BDRRule rule) {
    this.rule = rule;
  }

  @JsonIgnore
  public String getUnconvertedData() {
    return data;
  }

  public String getData() {
    if (this.targetType.equals(BDRTargetType.DAYTIME)) {
      return formatDayTimeTargetData(data);
    }
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public Date getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(Date updatedOn) {
    this.updatedOn = updatedOn;
  }

  public BdrTargetGroup getTargetGroup() {
    return targetGroup;
  }

  public void setTargetGroup(BdrTargetGroup targetGroup) {
    this.targetGroup = targetGroup;
  }

  @Transient
  public String getName() {
    return this.getTargetType().toString();
  }

  public void setName(String name) {}

  @PrePersist
  @PreUpdate
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((rule == null) ? 0 : rule.hashCode());
    result = prime * result + ((targetType == null) ? 0 : targetType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BDRTarget other = (BDRTarget) obj;
    if (rule != other.rule) return false;
    if (targetType != other.targetType) return false;
    return true;
  }

  private String formatDayTimeTargetData(String data) {
    String[] dataElements = StringUtils.split(data, ",");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < dataElements.length; i++) {
      sb.append(convertIntervalToHourMinuteFormat(dataElements[i]));
      if (i != dataElements.length - 1) {
        sb.append(",");
      }
    }

    return sb.toString();
  }

  private String convertIntervalToHourMinuteFormat(String dataElement) {
    String startHour;
    String startMinutes;
    String stopHour = "";
    String stopMinutes = "";
    String stopInterval;
    Instant start;
    Duration d;
    String[] dayTimeTarget = StringUtils.split(dataElement, "/");

    startHour = StringUtils.split(dayTimeTarget[1], ":")[0];
    startMinutes = StringUtils.split(dayTimeTarget[1], ":")[1];
    stopInterval = dayTimeTarget[2];

    if (!StringUtils.isEmpty(startHour) && !StringUtils.isEmpty(startMinutes)) {
      start =
          Instant.EPOCH
              .plus(Long.parseLong(startHour), ChronoUnit.HOURS)
              .plus(Long.parseLong(startMinutes), ChronoUnit.MINUTES);
      d = Duration.ofSeconds(start.getEpochSecond());
      d = d.plusMinutes(Long.parseLong(stopInterval));
      if (d.toHours() == 24L || d.toHours() == 0L) {
        stopHour = String.valueOf("00");
      } else {
        stopHour = String.valueOf(d.toHours());
      }

      d = d.minusHours(d.toHours());
      stopMinutes = d.toMinutes() != 0 ? String.valueOf(d.toMinutes()) : String.valueOf("00");
    }

    return new StringBuilder(dayTimeTarget[0])
        .append("/")
        .append(startHour)
        .append(":")
        .append(startMinutes)
        .append("/")
        .append(stopHour)
        .append(":")
        .append(stopMinutes)
        .toString();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public static class Cloner {

    private final BDRTarget target;
    private BdrTargetGroup targetGroup;

    public Cloner(BDRTarget target) {
      this.target = target;
    }

    public Cloner targetGroup(BdrTargetGroup targetGroup) {
      this.targetGroup = targetGroup;
      return this;
    }

    public BDRTarget build() {
      BDRTarget clone = new BDRTarget();
      clone.setData(target.data); // do not user target.getData()
      clone.setRule(target.rule);
      clone.setTargetType(target.targetType);
      clone.setTargetGroup(targetGroup);
      return clone;
    }
  }
}
