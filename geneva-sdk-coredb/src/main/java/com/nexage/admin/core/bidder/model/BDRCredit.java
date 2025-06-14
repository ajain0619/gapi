package com.nexage.admin.core.bidder.model;

import com.nexage.admin.core.model.Company;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "bdr_credit")
@NamedQuery(name = "getBdrCreditByCompany", query = " FROM BDRCredit bc where bc.company =:company")
public class BDRCredit implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  private Company company;

  @Column(name = "user", nullable = false, length = 100)
  private String user;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "transaction_date", nullable = false)
  private Date transactionDate;

  @PrePersist
  private void prePersist() {
    if (transactionDate == null) transactionDate = Calendar.getInstance().getTime();
  }

  public Long getPid() {
    return pid;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Date getTransactionDate() {
    return transactionDate;
  }

  public void setTransactionDate(Date transactionDate) {
    this.transactionDate = transactionDate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((amount == null) ? 0 : amount.hashCode());
    result = prime * result + ((company == null) ? 0 : company.hashCode());
    result = prime * result + ((transactionDate == null) ? 0 : transactionDate.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BDRCredit other = (BDRCredit) obj;
    if (amount == null) {
      if (other.amount != null) return false;
    } else if (!amount.equals(other.amount)) return false;
    if (company == null) {
      if (other.company != null) return false;
    } else if (!company.equals(other.company)) return false;
    if (transactionDate == null) {
      if (other.transactionDate != null) return false;
    } else if (!transactionDate.equals(other.transactionDate)) return false;
    if (user == null) {
      if (other.user != null) return false;
    } else if (!user.equals(other.user)) return false;
    return true;
  }
}
