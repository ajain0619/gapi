package com.nexage.admin.core.model;

import com.nexage.admin.core.model.Target.TargetType;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "audit")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Audit implements Serializable {

  private static final long serialVersionUID = -4253922859102575371L;

  public enum AuditProduct {
    ADSERVER
  }

  public enum AuditEntity {
    CAMPAIGN
  }

  public enum AuditProperty {
    STATUS,
    OVERALL_GOAL,
    DAILY_GOAL,
    FREQ_CAP_HOUR,
    FREQ_CAP_24,
    FREQ_CAP_LIFE,
    PRICE,
    DEPLOYMENT,
    END_DATE,
    COUNTRY_TARGETS,
    DEVICE_TARGETS,
    CUSTOM_TARGETS;

    public static AuditProperty getAuditPropertyForTargetType(TargetType targetType) {
      return TARGET_TYPE_AUDIT_PROPERTY_MAP.get(targetType);
    }
  }

  private static final EnumMap<TargetType, AuditProperty> TARGET_TYPE_AUDIT_PROPERTY_MAP =
      new EnumMap<>(
          Map.of(
              TargetType.ZONE,
              AuditProperty.DEPLOYMENT,
              TargetType.COUNTRY,
              AuditProperty.COUNTRY_TARGETS,
              TargetType.DEVICE,
              AuditProperty.DEVICE_TARGETS,
              TargetType.CUSTOM,
              AuditProperty.CUSTOM_TARGETS));

  public static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @EqualsAndHashCode.Include
  private Long pid;

  @Temporal(TemporalType.TIMESTAMP)
  private java.util.Date modifiedDate;

  @Column(name = "user_pid", nullable = false)
  @EqualsAndHashCode.Include
  private Long userPid;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AuditProduct product;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AuditEntity entity;

  @Column(name = "entity_pid", nullable = false)
  @EqualsAndHashCode.Include
  private Long entityPid;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AuditProperty property;

  @Column(name = "previous_value")
  private String previousValue;

  @Column(name = "new_value")
  private String newValue;

  public Audit(
      Date modifiedDate,
      Long userPid,
      AuditProduct product,
      AuditEntity entity,
      Long entityPid,
      AuditProperty property,
      String previousValue,
      String newValue) {
    this.modifiedDate = modifiedDate;
    this.userPid = userPid;
    this.product = product;
    this.entity = entity;
    this.entityPid = entityPid;
    this.property = property;
    this.previousValue = previousValue;
    this.newValue = newValue;
  }
}
