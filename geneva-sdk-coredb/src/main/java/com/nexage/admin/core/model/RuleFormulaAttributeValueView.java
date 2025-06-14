package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

@Table(name = "attribute_values")
@Immutable
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class RuleFormulaAttributeValueView implements Serializable {

  private static final long serialVersionUID = 7953599413696965122L;

  @Id @EqualsAndHashCode.Include @ToString.Include private Long pid;
  @Column @ToString.Include private String name;

  @Column(name = "attribute_pid")
  @ToString.Include
  private Long attributePid;
}
