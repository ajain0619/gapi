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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "site_metrics")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SiteMetrics implements Serializable {

  private static final long serialVersionUID = -961545137905350448L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "site_pid", referencedColumnName = "pid")
  @ToString.Exclude
  private Site site;

  @Column(name = "start_date")
  private Date startDate;

  @Column(name = "stop_date")
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

  @Column(name = "ecpm")
  private Double ecpm;

  @Column(name = "rpm")
  private Double rpm;

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

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private Date updatedOn;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_on")
  private Date createdOn;
}
