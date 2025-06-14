package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "attributes_company_visibility")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class AttributeCompanyVisibility implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "attribute_pid", nullable = false)
  private Long attributePid;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "company_pid", nullable = false)
  private Long companyPid;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;
}
