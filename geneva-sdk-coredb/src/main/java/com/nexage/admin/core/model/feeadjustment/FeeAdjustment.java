package com.nexage.admin.core.model.feeadjustment;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "fee_adjustment")
@Audited
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FeeAdjustment implements Serializable {

  private static final long serialVersionUID = -4132515643642985912L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid", nullable = false)
  @EqualsAndHashCode.Include
  private Long pid;

  @Column(name = "name", unique = true)
  @NotNull
  private String name;

  @Column(name = "inclusive")
  private Boolean inclusive;

  @Column(name = "demand_fee_adjustment")
  private Double demandFeeAdjustment;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "enabled")
  private Boolean enabled;

  @Column(name = "description")
  private String description;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update", nullable = false)
  private Date lastUpdate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = false)
  private Date creationDate;

  @OneToMany(
      mappedBy = "feeAdjustment",
      fetch = FetchType.LAZY,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  private List<FeeAdjustmentSeller> feeAdjustmentSellers = new ArrayList<>();

  @OneToMany(
      mappedBy = "feeAdjustment",
      fetch = FetchType.LAZY,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  private List<FeeAdjustmentBuyer> feeAdjustmentBuyers = new ArrayList<>();

  /** Invoked before insert to set the current time. */
  @PrePersist
  @PreUpdate
  public void prePersist() {
    lastUpdate = Date.from(Instant.now());
    creationDate = ObjectUtils.firstNonNull(creationDate, lastUpdate);
  }

  public void setFeeAdjustmentSellers(Collection<FeeAdjustmentSeller> feeAdjustmentSellers) {
    this.feeAdjustmentSellers.clear();
    this.feeAdjustmentSellers.addAll(feeAdjustmentSellers);
  }

  public void setFeeAdjustmentBuyers(Collection<FeeAdjustmentBuyer> feeAdjustmentBuyers) {
    this.feeAdjustmentBuyers.clear();
    this.feeAdjustmentBuyers.addAll(feeAdjustmentBuyers);
  }
}
