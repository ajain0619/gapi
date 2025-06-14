package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "rule")
@DiscriminatorFormula(
    "CASE WHEN rule_type = 4 THEN 'EXPERIMENT' "
        + "WHEN seller_seat_pid IS NOT NULL THEN 'SELLER_SEAT' "
        + "ELSE 'COMPANY' END")
@Where(clause = "status != -1")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public abstract class Rule implements Serializable {

  public static final int DEFAULT_PRIORITY = 3;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer version;

  @Column(name = "status", nullable = false)
  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Status status;

  @Column(name = "name", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @Column(name = "description")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String description;

  @Column(name = "rule_type")
  @Type(type = "com.nexage.admin.core.custom.type.RuleTypeEnumType")
  @EqualsAndHashCode.Include
  @ToString.Include
  private RuleType ruleType;

  @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, orphanRemoval = true)
  @Where(clause = "status >= 0")
  @OrderBy("pid asc")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Set<RuleTarget> ruleTargets = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "company_rule",
      joinColumns = @JoinColumn(name = "rule_pid"),
      inverseJoinColumns = @JoinColumn(name = "company_pid"))
  @AuditJoinTable(name = "company_rule_aud")
  @OrderBy("pid asc")
  private Set<RuleDeployedCompany> deployedCompanies = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "site_rule",
      joinColumns = @JoinColumn(name = "rule_pid"),
      inverseJoinColumns = @JoinColumn(name = "site_pid"))
  @AuditJoinTable(name = "site_rule_aud")
  @Where(clause = "status >= 0")
  @OrderBy("pid asc")
  private Set<RuleDeployedSite> deployedSites = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "position_rule",
      joinColumns = @JoinColumn(name = "rule_pid"),
      inverseJoinColumns = @JoinColumn(name = "position_pid"))
  @AuditJoinTable(name = "position_rule_aud")
  @Where(clause = "status >= 0")
  @OrderBy("pid asc")
  private Set<RuleDeployedPosition> deployedPositions = new HashSet<>();

  @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("pid asc")
  private Set<RuleIntendedAction> ruleIntendedActions = new HashSet<>();

  @OneToOne(
      mappedBy = "rule",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @EqualsAndHashCode.Include
  private RuleFormula ruleFormula;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update", nullable = false, updatable = true)
  @NotNull
  @ToString.Include
  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  private Date lastUpdate;

  @PrePersist
  @PreUpdate
  private void setLastUpdate() {
    lastUpdate = new Date();
  }
}
