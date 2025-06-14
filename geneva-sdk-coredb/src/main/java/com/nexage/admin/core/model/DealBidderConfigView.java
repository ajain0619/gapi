package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

/** Reference to the Bidder_Config table to grab bidders for Deal creation and edit */
@Immutable
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "bidder_config")
public class DealBidderConfigView implements Serializable {

  private static final long serialVersionUID = -776615327263161719L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "company_id")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long companyPid;

  @EqualsAndHashCode.Include @ToString.Include private String name;

  @Column(name = "traffic_status")
  @EqualsAndHashCode.Include
  @ToString.Include
  private boolean trafficStatus;
}
