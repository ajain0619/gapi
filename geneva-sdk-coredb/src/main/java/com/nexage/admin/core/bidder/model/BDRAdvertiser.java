package com.nexage.admin.core.bidder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.bidder.type.BDRStatus;
import com.nexage.admin.core.model.Company;
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
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Audited
@Table(
    name = "bdr_advertiser",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "company_pid"}))
public class BDRAdvertiser {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  @JsonIgnore
  @NotAudited
  private Company company;

  @Column(name = "name", length = 100)
  private String name;

  @Column(name = "status")
  @Type(type = "com.nexage.admin.core.bidder.usertype.BDRStatusUserType")
  private BDRStatus status = BDRStatus.ACTIVE;

  @Column(name = "adomain", length = 255)
  private String domainName;

  // Read only for sending it to client
  @Column(name = "company_pid", insertable = false, updatable = false)
  @NotAudited
  private Long companyPid;

  @Column(name = "iab_cat", length = 10)
  private String iabCategory;

  public Long getPid() {
    return pid;
  }

  public String getIabCategory() {
    return iabCategory;
  }

  public void setIabCategory(String iabCategory) {
    this.iabCategory = iabCategory;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BDRStatus getStatus() {
    return status;
  }

  public void setStatus(BDRStatus status) {
    this.status = status;
  }

  public Long getCompanyPid() {
    return companyPid;
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((company == null) ? 0 : company.hashCode());
    result = prime * result + ((domainName == null) ? 0 : domainName.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BDRAdvertiser other = (BDRAdvertiser) obj;
    if (company == null) {
      if (other.company != null) return false;
    } else if (!company.equals(other.company)) return false;
    if (domainName == null) {
      if (other.domainName != null) return false;
    } else if (!domainName.equals(other.domainName)) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (status != other.status) return false;
    return true;
  }
}
