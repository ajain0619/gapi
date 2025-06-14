package com.nexage.admin.core.model.feeadjustment;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Entity
@Table(
    name = "fee_adjustment_seller",
    uniqueConstraints = @UniqueConstraint(columnNames = {"fee_adjustment_pid", "seller_pid"}))
@Audited
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FeeAdjustmentSeller implements Serializable {

  private static final long serialVersionUID = -51529432776711554L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid", nullable = false)
  @EqualsAndHashCode.Include
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fee_adjustment_pid", referencedColumnName = "pid")
  private FeeAdjustment feeAdjustment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_pid", referencedColumnName = "pid")
  private FeeAdjustmentCompanyView seller;
}
