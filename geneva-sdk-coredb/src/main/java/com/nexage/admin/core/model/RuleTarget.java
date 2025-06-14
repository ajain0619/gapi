package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "rule_target")
@NamedQuery(
    name = "ruleTarget.findByRulePid",
    query = "SELECT rt FROM RuleTarget rt WHERE rt.rule.pid=:rulePid")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class RuleTarget implements Serializable {

  private static final long serialVersionUID = 6144302202768204206L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "status", nullable = false)
  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status;

  @Column(name = "match_type", nullable = false)
  @Type(type = "com.nexage.admin.core.custom.type.MatchTypeEnumType")
  @EqualsAndHashCode.Include
  @ToString.Include
  private MatchType matchType;

  @Column(name = "target_type", nullable = false)
  @Type(type = "com.nexage.admin.core.custom.type.RuleTargetTypeEnumType")
  @EqualsAndHashCode.Include
  @ToString.Include
  private RuleTargetType ruleTargetType;

  @Column(name = "data")
  @ToString.Include
  @EqualsAndHashCode.Include
  private String data;

  @ManyToOne
  @JoinColumn(name = "rule_pid", referencedColumnName = "pid")
  private Rule rule;
}
