package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Audited
@Table(name = "target")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class BaseTarget implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  @ToString.Include
  private Integer version;

  @Column(name = "data", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String data;

  @Column(name = "param_name")
  @ToString.Include
  private String paramName;

  @Column(name = "rule_type", nullable = false)
  @Type(type = "com.nexage.admin.core.custom.type.RuleEnumType")
  @EqualsAndHashCode.Include
  private RuleType ruleType;

  @Column(name = "target_type", nullable = false)
  @Type(type = "com.nexage.admin.core.custom.type.TargetEnumType")
  @EqualsAndHashCode.Include
  private TargetType targetType;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update", nullable = false, updatable = true)
  @NotNull
  private Date last_update;

  @PrePersist
  private void setCreateAndUpdate() {
    last_update = new Date();
  }

  @PreUpdate
  private void setUpdate() {
    last_update = new Date();
  }

  @AllArgsConstructor
  public enum RuleType {
    NEGATIVE(0),
    POSITIVE(1);

    private int code;

    public int asInt() {
      return code;
    }

    private static final HashMap<Integer, RuleType> fromIntMap = new HashMap<>();

    static {
      for (RuleType s : RuleType.values()) {
        fromIntMap.put(s.asInt(), s);
      }
    }

    public static RuleType fromInt(Integer i) {
      return fromIntMap.get(i);
    }
  }

  @AllArgsConstructor
  public enum TargetType {
    COUNTRY_STATE(0),
    GENDER_AGE(1),
    DEVICE_MAKE_MODEL(2),
    DEVICE_OS_VERSION(3),
    SDK_VERSION(4);

    private int code;

    public int asInt() {
      return code;
    }

    private static final HashMap<Integer, TargetType> fromIntMap = new HashMap<>();

    static {
      for (TargetType s : TargetType.values()) {
        fromIntMap.put(s.asInt(), s);
      }
    }

    public static TargetType fromInt(Integer i) {
      return fromIntMap.get(i);
    }
  }
}
