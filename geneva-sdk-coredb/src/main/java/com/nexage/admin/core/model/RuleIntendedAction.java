package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.RuleActionType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "rule_intended_action")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class RuleIntendedAction implements Serializable {
  private static final long serialVersionUID = 8426315597959887156L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update", nullable = false)
  @NotNull
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Date lastUpdate;

  @ManyToOne
  @JoinColumn(name = "rule_pid", referencedColumnName = "pid")
  private Rule rule;

  @Column(name = "action_type")
  @Type(type = "com.nexage.admin.core.custom.type.RuleActionTypeEnumType")
  @EqualsAndHashCode.Include
  @ToString.Include
  private RuleActionType actionType;

  @Column(name = "action_data")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String actionData;

  public RuleIntendedAction(Rule rule, RuleActionType actionType, String actionData) {
    this.rule = rule;
    this.actionType = actionType;
    this.actionData = actionData;
  }

  @PrePersist
  @PreUpdate
  private void setLastUpdate() {
    lastUpdate = new Date();
  }
}
