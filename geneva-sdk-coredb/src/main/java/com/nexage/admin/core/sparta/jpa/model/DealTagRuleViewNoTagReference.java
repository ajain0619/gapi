package com.nexage.admin.core.sparta.jpa.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

@Immutable
@Table(name = "tag_rule")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DealTagRuleViewNoTagReference implements Serializable {

  private static final long serialVersionUID = -691784899241415552L;

  @Id
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  private Long pid;

  @Column(name = "target")
  @ToString.Include
  private String target;

  @Column(name = "target_type")
  @ToString.Include
  private String targetType;

  @Column(name = "rule_type")
  @ToString.Include
  private String ruleType;
}
