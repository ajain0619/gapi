package com.nexage.admin.core.bidder.model.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;

@Immutable
@Entity
@Table(name = "bdr_advertiser")
public class BDRAdvertiserView {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "company_pid")
  private Long companyPid;

  @Column(name = "name", length = 100)
  private String name;

  public Long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public Long getCompanyPid() {
    return companyPid;
  }
}
