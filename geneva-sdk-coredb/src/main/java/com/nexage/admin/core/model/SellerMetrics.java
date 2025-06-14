package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(
    name = "seller_metrics",
    uniqueConstraints = @UniqueConstraint(columnNames = {"company_pid", "start_date", "stop_date"}))
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SellerMetrics implements Serializable {

  private static final long serialVersionUID = 4050886665322014025L;

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @Id
  @EqualsAndHashCode.Include
  @ToString.Include
  protected Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  private Company company;

  @NotNull
  @Column(name = "start_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date startDate;

  @NotNull
  @Column(name = "stop_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date stopDate;

  @Column(name = "ad_clicked")
  private Long adClicked;

  @Column(name = "ad_requested")
  private Long adRequested;

  @Column(name = "ad_served")
  private Long adServed;

  @Column(name = "ad_delivered")
  private Long adDelivered;

  @Column(name = "fill_rate")
  private Double fillRate;

  @Column(name = "ctr")
  private Double ctr;

  @Column(name = "rpm")
  private Double rpm;

  @Column(name = "ecpm")
  private Double ecpm;

  @Column(name = "total_ecpm")
  private Double totalEcpm;

  @Column(name = "total_rpm")
  private Double totalRpm;

  @Column(name = "seller_revenue")
  private Double sellerRevenue;

  @Column(name = "total_revenue")
  private Double totalRevenue;

  @Column(name = "verizon_revenue")
  private Double verizonRevenue;

  @Column(name = "created_on")
  private Date createdOn;

  @Column(name = "updated_on")
  private Date updatedOn;
}
