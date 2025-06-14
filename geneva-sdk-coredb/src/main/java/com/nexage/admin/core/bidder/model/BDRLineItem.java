package com.nexage.admin.core.bidder.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.bidder.type.BDRFreqCapMode;
import com.nexage.admin.core.bidder.type.BDRLineItemStatus;
import com.nexage.admin.core.bidder.type.BDRLineItemType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Audited
@Table(name = "bdr_lineitem")
public class BDRLineItem {

  private static String formatString = "yyyy-MM-dd'T'HH:mm:ssXXX";
  private static DateTimeFormatter format = DateTimeFormatter.ofPattern(formatString);

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "ext_id", length = 32, unique = true)
  private String externalId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "insertionorder_pid")
  @JsonBackReference
  private BdrInsertionOrder insertionOrder;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "lineItem")
  @AuditMappedBy(mappedBy = "lineItem")
  private Set<BdrTargetGroup> targetGroups = new HashSet<>();

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(
      name = "bdr_lineitem_creative",
      joinColumns = {@JoinColumn(name = "lineitem_pid", referencedColumnName = "pid")},
      inverseJoinColumns = {@JoinColumn(name = "creative_pid", referencedColumnName = "pid")})
  @JsonIgnore
  @NotAudited
  private BdrCreative creative;

  @Column(name = "name", length = 100)
  @NotNull
  private String name;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "start")
  @JsonIgnore
  @NotNull
  private Date startDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "stop")
  @JsonIgnore
  private Date stopDate;

  @Column(name = "status")
  @Type(type = "com.nexage.admin.core.bidder.usertype.BDRLineItemStatusUserType")
  @NotNull
  private BDRLineItemStatus status = BDRLineItemStatus.INACTIVE;

  @Column(name = "spend")
  private BigDecimal spend;

  @Column(name = "impressions")
  private Long impressions;

  @Column(name = "type")
  @Enumerated(EnumType.ORDINAL)
  @NotNull
  private BDRLineItemType type;

  @Column(name = "cap_daily_imp")
  private Long dailyImpressionCap;

  @Column(name = "cap_daily_spend")
  private BigDecimal dailySpendCap;

  @Transient private BDRFreqCapMode frequencyCapMode;

  @Column(name = "freq_cap_mode")
  @NotNull
  private int freqCapModeValue;

  @Column(name = "freq_day")
  private Long frequencyPerDay;

  @Column(name = "freq_week")
  private Long frequencyPerWeek;

  @Column(name = "freq_month")
  private Long frequencyPerMonth;

  @Column(name = "freq_life")
  private Long frequencyPerLife;

  @Column(name = "app", length = 100)
  private String app;

  @Column(name = "deployable")
  private boolean deployable = true;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @JsonIgnore
  private Date updatedOn;

  @Transient private String start;
  @Transient private String stop;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "resume_time")
  @Temporal(TemporalType.TIMESTAMP)
  @JsonIgnore
  @NotAudited
  private Date resumeTime;

  @Column(name = "resume_progress")
  @JsonIgnore
  @NotAudited
  private Double resumeProgress;

  @PrePersist
  @PreUpdate
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }

  public BdrInsertionOrder getInsertionOrder() {
    return insertionOrder;
  }

  public void setInsertionOrder(BdrInsertionOrder insertionOrder) {
    this.insertionOrder = insertionOrder;
  }

  public void addTargetGroup(BdrTargetGroup targetGroup) {
    this.targetGroups.add(targetGroup);
  }

  public void setTargetGroups(Set<BdrTargetGroup> targetGroups) {
    this.targetGroups = targetGroups;
  }

  public Set<BdrTargetGroup> getTargetGroups() {
    return targetGroups;
  }

  public Long getPid() {
    return pid;
  }

  public BdrCreative getCreative() {
    return creative;
  }

  public void setCreative(BdrCreative creative) {
    this.creative = creative;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getStopDate() {
    return stopDate;
  }

  public void setStopDate(Date stopDate) {
    this.stopDate = stopDate;
  }

  public BDRLineItemStatus getStatus() {
    return status;
  }

  public void setStatus(BDRLineItemStatus status) {
    this.status = status;
  }

  public BigDecimal getSpend() {
    return spend;
  }

  public void setSpend(BigDecimal spend) {
    this.spend = spend;
  }

  public Long getImpressions() {
    return impressions;
  }

  public void setImpressions(Long impressions) {
    this.impressions = impressions;
  }

  public BDRLineItemType getType() {
    return type;
  }

  public void setType(BDRLineItemType type) {
    this.type = type;
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

  public BDRFreqCapMode getFrequencyCapMode() {
    switch (freqCapModeValue) {
      case 1:
        if (frequencyPerDay == null
            && frequencyPerWeek == null
            && frequencyPerMonth == null
            && frequencyPerLife == null) {
          frequencyCapMode = BDRFreqCapMode.NONE;
        } else {
          frequencyCapMode = BDRFreqCapMode.ALWAYS;
        }
        break;
      case 2:
        frequencyCapMode = BDRFreqCapMode.WHEN_POSSIBLE;
        break;
    }
    return frequencyCapMode;
  }

  public void setFrequencyCapMode(BDRFreqCapMode frequencyCapMode) {
    this.frequencyCapMode = frequencyCapMode;
    this.freqCapModeValue = frequencyCapMode.getCode();
  }

  public Long getFrequencyPerDay() {
    return frequencyPerDay;
  }

  public void setFrequencyPerDay(Long frequencyPerDay) {
    this.frequencyPerDay = frequencyPerDay;
  }

  public Long getFrequencyPerWeek() {
    return frequencyPerWeek;
  }

  public void setFrequencyPerWeek(Long frequencyPerWeek) {
    this.frequencyPerWeek = frequencyPerWeek;
  }

  public Long getFrequencyPerMonth() {
    return frequencyPerMonth;
  }

  public void setFrequencyPerMonth(Long frequencyPerMonth) {
    this.frequencyPerMonth = frequencyPerMonth;
  }

  public Long getFrequencyPerLife() {
    return frequencyPerLife;
  }

  public void setFrequencyPerLife(Long frequencyPerLife) {
    this.frequencyPerLife = frequencyPerLife;
  }

  public String getApp() {
    return app;
  }

  public void setApp(String app) {
    this.app = app;
  }

  public boolean isDeployable() {
    return deployable;
  }

  public void setDeployable(boolean deployable) {
    this.deployable = deployable;
  }

  public Date getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(Date updatedOn) {
    this.updatedOn = updatedOn;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  @Transient
  public void setInsertionOrderPid(Long pid) {}

  @Transient
  public Long getInsertionOrderPid() {
    return insertionOrder != null ? insertionOrder.getPid() : null;
  }

  public String getStart() {
    if (start == null && startDate != null) {
      start = format.format(ZonedDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault()));
    }
    return start;
  }

  public void setStart(String start) throws ParseException {
    this.start = start;
    SimpleDateFormat sdf = new SimpleDateFormat(formatString);
    this.startDate = sdf.parse(start);
  }

  public String getStop() {
    if (stop == null && stopDate != null) {
      stop = format.format(ZonedDateTime.ofInstant(stopDate.toInstant(), ZoneId.systemDefault()));
    }
    return stop;
  }

  public void setStop(String stop) throws ParseException {
    if (stop != null) {
      this.stop = stop;
      SimpleDateFormat sdf = new SimpleDateFormat(formatString);
      this.stopDate = sdf.parse(stop);
    }
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public static Date parse(String date) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat(formatString);
    return sdf.parse(date);
  }

  public static String format(Date date) {
    return format.format(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public static class Cloner {
    private BdrInsertionOrder insertionOrder;
    private final BDRLineItem lineItem;
    private Set<Long> targetGroupPids;

    public Cloner(BDRLineItem lineItem) {
      this.lineItem = lineItem;
    }

    public Cloner insertionOrder(BdrInsertionOrder insertionOrder) {
      this.insertionOrder = insertionOrder;
      return this;
    }

    public Cloner targetGroupPids(Set<Long> targetGroupPids) {
      this.targetGroupPids = targetGroupPids;
      return this;
    }

    public BDRLineItem build() {
      BDRLineItem clone = new BDRLineItem();
      clone.setApp(lineItem.getApp());
      clone.setCreative(lineItem.getCreative());
      clone.setDailyImpressionCap(lineItem.getDailyImpressionCap());
      clone.setDailySpendCap(lineItem.getDailySpendCap());
      clone.setDeployable(lineItem.isDeployable());
      clone.setFrequencyCapMode(lineItem.getFrequencyCapMode());
      clone.setFrequencyPerDay(lineItem.getFrequencyPerDay());
      clone.setFrequencyPerLife(lineItem.getFrequencyPerLife());
      clone.setFrequencyPerMonth(lineItem.getFrequencyPerMonth());
      clone.setFrequencyPerWeek(lineItem.getFrequencyPerWeek());
      clone.setImpressions(lineItem.getImpressions());
      clone.setInsertionOrder(insertionOrder);
      clone.setName("Clone of " + lineItem.getName());
      clone.setSpend(lineItem.getSpend());
      clone.setStartDate(lineItem.getStartDate());
      clone.setStopDate(lineItem.getStopDate());
      clone.setType(lineItem.getType());

      // only copy creatives if the LI is being cloned to the same advertiser
      boolean copyCreatives =
          lineItem.getInsertionOrder().getAdvertiser().equals(insertionOrder.getAdvertiser());

      // status is left to inactive (default)
      if (lineItem.getTargetGroups() != null && targetGroupPids != null) {
        for (BdrTargetGroup targetGroup : lineItem.getTargetGroups()) {
          if (targetGroupPids.contains(targetGroup.getPid())) {
            BdrTargetGroup cloneTargetGroup =
                new BdrTargetGroup.Cloner(targetGroup)
                    .lineItem(clone)
                    .copyCreatives(copyCreatives)
                    .build();
            clone.addTargetGroup(cloneTargetGroup);
          }
        }
      }
      return clone;
    }
  }

  public Date getResumeTime() {
    return resumeTime;
  }

  public void setResumeTime(Date resumeTime) {
    this.resumeTime = resumeTime;
  }

  public Double getResumeProgress() {
    return resumeProgress;
  }

  public void setResumeProgress(Double resumeProgress) {
    this.resumeProgress = resumeProgress;
  }
}
