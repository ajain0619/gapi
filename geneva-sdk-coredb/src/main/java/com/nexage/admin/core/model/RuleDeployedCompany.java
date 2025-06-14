package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

@Entity
@Immutable
@Audited
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
public class RuleDeployedCompany implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(nullable = false)
  @Id
  protected Long pid;

  @Column(name = "name", nullable = false, length = 100)
  @NotNull
  private String name;
}
