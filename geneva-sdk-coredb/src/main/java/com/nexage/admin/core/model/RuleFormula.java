package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

/** <code>RuleFormula</code> */
@Audited
@Table(name = "rule_formula")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class RuleFormula implements Serializable {

  private static final long serialVersionUID = -6802590426328272053L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  @EqualsAndHashCode.Include
  private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @EqualsAndHashCode.Include
  @Column(name = "last_update", nullable = false)
  @NotNull
  private Date lastUpdate;

  @Column(name = "auto_update")
  @EqualsAndHashCode.Include
  private boolean autoUpdate;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "rule_pid", referencedColumnName = "pid", updatable = false)
  private CompanyRule rule;

  @Lob
  @Column(name = "formula", length = 2000)
  @EqualsAndHashCode.Include
  private String formula;

  @PrePersist
  @PreUpdate
  private void setLastUpdate() {
    lastUpdate = new Date();
  }

  public RuleFormula(CompanyRule rule, String formula) {
    this.rule = rule;
    this.formula = formula;
  }
}
