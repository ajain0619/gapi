package com.nexage.admin.core.model;

import com.nexage.admin.core.model.filter.Domain;
import com.nexage.admin.core.model.placementformula.formula.impl.Operator;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Table(name = "deal_domain")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DealDomain implements Serializable {

  private static final long serialVersionUID = 7187900742874573181L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "deal_pid")
  private long dealPid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "domain_pid", referencedColumnName = "pid")
  private Domain domain;

  @Column(name = "operator")
  @Enumerated(EnumType.STRING)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Operator operator;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_on")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Date createdOn;

  @Version @ToString.Include private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @ToString.Include
  private Date updatedOn;
}
