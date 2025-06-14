package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nexage.admin.core.model.Audit.AuditEntity;
import com.nexage.admin.core.model.Audit.AuditProduct;
import com.nexage.admin.core.model.Audit.AuditProperty;
import com.nexage.admin.core.model.Target.TargetType;
import com.nexage.admin.core.util.Auditable;
import com.nexage.admin.core.util.UUIDGenerator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Formula;

@Table(name = "as_campaign")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Campaign implements CampaignSummary, Serializable, Auditable {

  /** */
  private static final long serialVersionUID = -6473057687302199844L;

  private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @ToString.Include
  private Long pid;

  @Column(name = "seller_id", nullable = false)
  private long sellerId;

  @Column(name = "advertiser_id", nullable = false)
  private long advertiserId;

  @Column(name = "ext_id", nullable = false)
  private String externalId;

  @Column(length = 100, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @Column(nullable = false)
  @EqualsAndHashCode.Include
  private CampaignType type;

  @Column(nullable = false)
  private CampaignModel model;

  @Column(nullable = false)
  private BigDecimal price;

  @Column private Long goal;

  @Column private Long daily;

  @Column(name = "cap_hour")
  private long capHour;

  @Column(name = "cap_24")
  private long cap24;

  @Column(name = "cap_life")
  private long capLife;

  @Column private BigDecimal bias;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "start", nullable = false)
  private Date start;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "stop", nullable = true)
  private Date stop;

  @Transient private String startAdjusted;

  @Transient private String stopAdjusted;

  @Column(nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private CampaignStatus status = CampaignStatus.INACTIVE;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private java.util.Date lastUpdate;

  @Transient private long impressions;

  @Transient private long delivered;

  @Transient private long clicks;

  @Transient private BigDecimal clickThroughRate;

  @JsonIgnore
  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "campaignId",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Collection<Target> targets = new ArrayList<>();

  // @JsonManagedReference
  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "campaignCreativePk.campaign",
      cascade = CascadeType.ALL)
  private Collection<CampaignCreative> campaignCreatives = new ArrayList<>();

  @Transient private boolean inFlight = false;

  private boolean deployable = false;

  @Formula(value = "(select a.name from as_advertiser a where a.pid = advertiser_id)")
  private String advertiserName;

  public Campaign(long pid) {
    this.pid = pid;
  }

  public Collection<Target> getTargets() {
    return targets;
  }

  public void setTargets(Collection<Target> targets) {
    this.targets.clear();
    this.targets.addAll(targets);
  }

  public void addTarget(Target target) {
    target.setCampaignId(pid);
    target.setSellerId(sellerId);
    target.setLastUpdate(new Date());

    targets.add(target);
  }

  public void removeTargetsOfType(TargetType targetType) {
    if (targets != null) {
      Iterator<Target> iterator = targets.iterator();
      while (iterator.hasNext()) {
        if (iterator.next().getType().equals(targetType)) {
          iterator.remove();
        }
      }
    }
  }

  @Override
  public boolean isPurgeable() {
    boolean retVal = false;
    if (getStatus() == CampaignStatus.PAUSED || getStatus() == CampaignStatus.ACTIVE) {
      retVal = false;
    } else {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.HOUR_OF_DAY, -1);
      Date oneHourAgo = calendar.getTime();
      if (lastUpdate.before(oneHourAgo)) {
        retVal = true;
      }
    }
    return retVal;
  }

  @PrePersist
  protected void createExternalId() {
    externalId = (String) new UUIDGenerator().generate();
    updateLastUpdated();
  }

  @PreUpdate
  protected void updateLastUpdated() {
    lastUpdate = new Date();
  }

  @PostLoad
  public void calculateInFlight() {
    boolean currentlyRunning = false;

    if (CampaignStatus.ACTIVE.equals(status)) {
      Date now = new Date();
      currentlyRunning =
          (start.getTime() < now.getTime()) && (null == stop || stop.getTime() > now.getTime());
    }

    inFlight = currentlyRunning;
  }

  public void calculateDeployability() {
    boolean currentlyDeployable = true;

    // at least one creative must be present
    if (0 == campaignCreatives.size()) {
      currentlyDeployable = false;
    }

    // end date, if it exists, must be no earlier than the start date
    if ((stop != null) && (stop.getTime() < start.getTime())) {
      currentlyDeployable = false;
    }

    // campaign must be deployable to exactly one zone
    List<Target> targets = getTargetsOfType(TargetType.ZONE);
    if (targets.size() != 1 || 0 == targets.get(0).getFilter().length()) {
      currentlyDeployable = false;
    }

    deployable = currentlyDeployable;
  }

  /**
   * Gets a target of a given type.
   *
   * @param targetType - type of target
   * @return list of targets corresponding to targetType
   */
  public List<Target> getTargetsOfType(TargetType targetType) {
    List<Target> targetsOfType = new ArrayList<>();

    if (targetType != null && targets != null && !targets.isEmpty()) {
      for (Target t : targets) {
        if (targetType.equals(t.getType())) {
          targetsOfType.add(t);
        }
      }
    }

    return targetsOfType;
  }

  @Override
  @JsonIgnore
  public AuditProduct getAuditProduct() {
    return AuditProduct.ADSERVER;
  }

  @Override
  @JsonIgnore
  public AuditEntity getAuditEntity() {
    return AuditEntity.CAMPAIGN;
  }

  @Override
  @JsonIgnore
  public Map<AuditProperty, String> getAuditableData() {
    Map<AuditProperty, String> auditData = new HashMap<AuditProperty, String>();

    auditData.put(AuditProperty.STATUS, (null == status ? null : status.toString()));
    auditData.put(AuditProperty.OVERALL_GOAL, (null == goal ? null : goal.toString()));
    auditData.put(AuditProperty.DAILY_GOAL, (null == daily ? null : daily.toString()));
    auditData.put(AuditProperty.FREQ_CAP_HOUR, String.valueOf(capHour));
    auditData.put(AuditProperty.FREQ_CAP_24, String.valueOf(cap24));
    auditData.put(AuditProperty.FREQ_CAP_LIFE, String.valueOf(capLife));
    auditData.put(
        AuditProperty.PRICE, (null == price ? null : String.valueOf(price.doubleValue())));
    auditData.put(
        AuditProperty.END_DATE,
        (null == stop
            ? null
            : Audit.DATE_FORMAT.format(
                ZonedDateTime.ofInstant(stop.toInstant(), ZoneId.systemDefault()))));

    // auditing all types of targets
    StringBuilder builder;
    List<Target> targets;
    for (TargetType targetType : TargetType.values()) {

      builder = new StringBuilder();
      targets = getTargetsOfType(targetType);

      if (targets != null) {

        // get all target filters
        List<String> filters = new ArrayList<String>();
        for (Target target : targets) {
          filters.add(target.getSortedFilter());
        }

        // sort and collect all filters
        Collections.sort(filters);
        for (String filter : filters) {
          if (builder.length() > 0) {
            builder.append(Target.TARGET_DELIMITER);
          }
          builder.append(filter);
        }
      }

      // the next line looks like unnecessary logic but is required due to legacy target
      // functionality (nwhalen)
      String auditValue = (0 == builder.length() ? null : builder.toString());
      auditData.put(AuditProperty.getAuditPropertyForTargetType(targetType), auditValue);
    }

    return auditData;
  }

  public String getStartAdjusted() {
    if (startAdjusted == null && start != null) {
      startAdjusted =
          format.format(ZonedDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()));
    }
    return startAdjusted;
  }

  public String getStopAdjusted() {
    if (stopAdjusted == null && stop != null) {
      stopAdjusted =
          format.format(ZonedDateTime.ofInstant(stop.toInstant(), ZoneId.systemDefault()));
    }
    return stopAdjusted;
  }

  @Override
  public void addTimeZoneOffset() {}

  @Override
  public void removeTimeZoneOffset() {}

  /**
   * Returns a status value that is not necessarily the ordinal of the enumeration.
   *
   * @return status value
   */
  @JsonIgnore
  public int getExternalStatus() {
    return status.getExternalValue();
  }

  @JsonIgnore
  public int getExternalType() {
    return type.getExternalValue();
  }

  @JsonIgnore
  public int getExternalModel() {
    return model.getExternalValue();
  }

  @AllArgsConstructor
  @Getter
  public enum CampaignType {
    REMNANT(0),
    SPONSORSHIP(1),
    GUARANTEED(2);

    private final int externalValue;
  }

  @AllArgsConstructor
  @Getter
  public enum CampaignModel {
    CPM(0),
    CPC(1),
    CPI(2);

    private final int externalValue;
  }

  @AllArgsConstructor
  @Getter
  public enum CampaignStatus {
    INACTIVE(0),
    ACTIVE(1),
    PAUSED(2),
    COMPLETED(3),
    DELETED(-1);

    private final int externalValue;
  }
}
