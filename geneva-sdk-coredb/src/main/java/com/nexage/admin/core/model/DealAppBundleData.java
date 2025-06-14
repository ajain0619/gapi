package com.nexage.admin.core.model;

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

@Table(name = "deal_app_bundle_data")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DealAppBundleData implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "operator")
  @Enumerated(EnumType.STRING)
  private Operator operator;

  @Column(name = "deal_pid")
  private long dealPid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "app_bundle_data_pid", referencedColumnName = "pid")
  private AppBundleData appBundleData;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private Date updatedOn;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_on")
  private Date createdOn;

  @Version private Integer version;
}
