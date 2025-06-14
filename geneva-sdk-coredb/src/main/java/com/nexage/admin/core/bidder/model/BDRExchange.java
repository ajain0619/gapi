package com.nexage.admin.core.bidder.model;

import com.nexage.admin.core.bidder.type.BDRStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "bdr_exchange")
public class BDRExchange implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "ext_id", length = 32, unique = true)
  private String externalId;

  @Column(name = "name", length = 100)
  private String name;

  // NOTE using a property of handler conflicts with javassist bytecode rewriting, hence the
  // dropping of "a"
  @Column(name = "handler", length = 20)
  private String hndler;

  @Column(name = "status")
  @Type(type = "com.nexage.admin.core.bidder.usertype.BDRStatusUserType")
  private BDRStatus status = BDRStatus.ACTIVE;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  private Date updatedOn;

  @Column(name = "nexage_id")
  private Long nexageId;

  @Column(name = "bidding_fee", columnDefinition = "decimal(19,2)")
  private BigDecimal exchangeBiddingFee;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "bdr_exchange_company", joinColumns = @JoinColumn(name = "exchange_pid"))
  @MapKeyColumn(name = "company_pid")
  // must use name bidding_fee because that is the name of the column
  @Column(name = "bidding_fee", columnDefinition = "decimal(19,2)")
  private Map<Long, BigDecimal> bidding_fee = new HashMap<>();

  @Column(name = "bidder_site")
  private Integer bidderSite;

  @Column(name = "booking_event")
  private Integer bookingEvent;

  @Column(name = "tmax")
  private Integer tmax;

  @Column(name = "tmargin")
  private Integer tmargin;

  @PrePersist
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }

  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHndler() {
    return hndler;
  }

  public void setHndler(String hndler) {
    this.hndler = hndler;
  }

  public BDRStatus getStatus() {
    return status;
  }

  public void setStatus(BDRStatus status) {
    this.status = status;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Date getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(Date updatedOn) {
    this.updatedOn = updatedOn;
  }

  public Long getNexageId() {
    return nexageId;
  }

  public void setNexageId(Long nexageId) {
    this.nexageId = nexageId;
  }

  public BigDecimal getExchangeBiddingFee() {
    return exchangeBiddingFee;
  }

  public void setExchangeBiddingFee(BigDecimal biddingFee) {
    this.exchangeBiddingFee = biddingFee;
  }

  public Map<Long, BigDecimal> getCompanyBiddingFees() {
    return bidding_fee;
  }

  public void setCompanyBiddingFees(Map<Long, BigDecimal> fees) {
    bidding_fee = fees;
  }

  public Integer getBidderSite() {
    return bidderSite;
  }

  public void setBidderSite(Integer bidderSite) {
    this.bidderSite = bidderSite;
  }

  public Integer getBookingEvent() {
    return bookingEvent;
  }

  public void setBookingEvent(Integer bookingEvent) {
    this.bookingEvent = bookingEvent;
  }

  public Integer getTmax() {
    return tmax;
  }

  public void setTmax(Integer tmax) {
    this.tmax = tmax;
  }

  public Integer getTmargin() {
    return tmargin;
  }

  public void setTmargin(Integer tmargin) {
    this.tmargin = tmargin;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
    result = prime * result + ((hndler == null) ? 0 : hndler.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((nexageId == null) ? 0 : nexageId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BDRExchange other = (BDRExchange) obj;
    if (externalId == null) {
      if (other.externalId != null) return false;
    } else if (!externalId.equals(other.externalId)) return false;
    if (hndler == null) {
      if (other.hndler != null) return false;
    } else if (!hndler.equals(other.hndler)) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (status != other.status) return false;
    if (nexageId == null) {
      if (other.nexageId != null) return false;
    } else if (!nexageId.equals(other.nexageId)) return false;
    return true;
  }
}
