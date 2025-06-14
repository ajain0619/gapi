package com.nexage.admin.core.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Audited
@DiscriminatorValue("COMPANY")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class CompanyRule extends Rule {

  private static final long serialVersionUID = 1L;

  @Column(name = "company_pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long ownerCompanyPid;
}
