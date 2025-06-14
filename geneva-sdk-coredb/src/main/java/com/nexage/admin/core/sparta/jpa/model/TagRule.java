package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.model.Tag;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Table(name = "tag_rule")
@Data
@Entity
@Audited
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TagRule implements Serializable {

  private static final long serialVersionUID = 9125368604323987753L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  protected Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  private Integer version;

  @Column(name = "tag_pid", insertable = false, updatable = false)
  @NotAudited
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long tagPid;

  @ManyToOne
  @JoinColumn(name = "tag_pid", referencedColumnName = "pid")
  @NotNull
  @JsonIgnore
  @JsonBackReference
  private Tag tag;

  @Column(name = "target", nullable = false)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private String target;

  @Column(name = "param_name")
  private String paramName;

  @Column(name = "rule_type", nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private RuleType ruleType;

  @Column(name = "target_type", nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private TargetType targetType;

  public enum RuleType {
    UserAgent,
    Country,
    URL,
    KeywordParam,
    ReqParam,
    DeviceModel,
    DeviceMake,
    ISPCarrier,
    DMA,
    OsVersion,
    DeviceMakeModel,
    CarrierWifi,
    SmartYield,
    SdkPlugin,
    SdkCapability;
  }

  public enum TargetType {
    Keyword,
    NegKeyword,
    Regex,
    NegRegex,
  }
}
