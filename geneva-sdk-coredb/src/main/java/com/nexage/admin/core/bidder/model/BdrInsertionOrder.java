package com.nexage.admin.core.bidder.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.bidder.type.BDRInsertionOrderType;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Audited
@Table(name = "bdr_insertionorder")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BdrInsertionOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @Setter(AccessLevel.NONE)
  private Long pid;

  @Column(name = "ref_number", length = 32)
  @EqualsAndHashCode.Include
  private String refNumber;

  @Column(name = "name", length = 100)
  @EqualsAndHashCode.Include
  private String name;

  @Column(name = "advertiser_pid", insertable = false, updatable = false)
  @NotAudited
  @EqualsAndHashCode.Include
  private Long advertiserPid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "advertiser_pid", referencedColumnName = "pid")
  @JsonIgnore
  @JsonBackReference
  private BDRAdvertiser advertiser;

  @Column(name = "type")
  @Type(type = "com.nexage.admin.core.bidder.usertype.BDRInsertionOrderTypeUserType")
  @EqualsAndHashCode.Include
  private BDRInsertionOrderType type = BDRInsertionOrderType.NEXAGE;

  @Column(name = "adomain", length = 255)
  @EqualsAndHashCode.Include
  private String adomain;

  @Column(name = "comments", length = 255)
  @EqualsAndHashCode.Include
  private String comments;

  @Column(name = "bid_selector", nullable = false)
  private BigDecimal bidSelector = BigDecimal.valueOf(0.0);

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "insertionOrder")
  @JsonIgnore
  @AuditMappedBy(mappedBy = "insertionOrder")
  private Set<BDRLineItem> lineItems = new HashSet<>();

  @Column(name = "iab_cat", length = 10)
  @EqualsAndHashCode.Include
  private String iabCategory;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @JsonIgnore
  private Date updatedOn;

  @Transient private String advertiserName;

  @PrePersist
  @PreUpdate
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }

  public String getAdomain() {
    if (adomain == null && advertiser != null) {
      // default to advertiser's
      adomain = advertiser.getDomainName();
    }
    return adomain;
  }

  public void addLineItem(BDRLineItem lineItem) {
    lineItem.setInsertionOrder(this);
    lineItems.add(lineItem);
  }

  public String getIabCategory() {
    if (iabCategory == null && advertiser != null) {
      // default to advertiser's
      iabCategory = advertiser.getIabCategory();
    }
    return iabCategory;
  }

  public String getAdvertiserName() {
    if (advertiserName == null) {
      advertiserName = advertiserPid != null ? advertiser.getName() : null;
    }
    return advertiserName;
  }
}
