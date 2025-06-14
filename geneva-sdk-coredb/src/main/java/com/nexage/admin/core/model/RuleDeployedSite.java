package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

@Entity
@Immutable
@Audited
@Table(name = "site")
@Getter
@Setter
@NoArgsConstructor
public class RuleDeployedSite implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id private Long pid;

  @Column(name = "company_pid", insertable = false, updatable = false)
  private Long companyPid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  @NotNull
  private RuleDeployedCompany company;

  @Column(nullable = false)
  @Size(max = 255)
  @NotNull
  private String name;
}
