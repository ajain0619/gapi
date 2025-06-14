package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "buyer_group")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class BuyerGroup implements Serializable {

  private static final long serialVersionUID = -5736679912298720574L;

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(length = 255, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Company company;

  @Column(name = "sfdc_line_id", length = 25, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String sfdcLineId;

  @Column(name = "sfdc_io_id", length = 25, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String sfdcIoId;

  @Deprecated
  @Column(length = 3, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String currency;

  @Column(name = "billing_country", length = 3, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String billingCountry;

  @Column(nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private boolean billable;

  @Version private Integer version;

  @OneToMany(mappedBy = "buyerGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<BuyerSeat> buyerSeats = new ArrayList<>();

  public BuyerGroup(
      String name,
      String sfdcLineId,
      String sfdcIoId,
      String currency,
      String billingCountry,
      boolean billable) {
    this.name = name;
    this.sfdcLineId = sfdcLineId;
    this.sfdcIoId = sfdcIoId;
    this.currency = currency;
    this.billingCountry = billingCountry;
    this.billable = billable;
  }
}
