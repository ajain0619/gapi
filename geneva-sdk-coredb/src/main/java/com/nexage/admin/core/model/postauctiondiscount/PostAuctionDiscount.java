package com.nexage.admin.core.model.postauctiondiscount;

import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "post_auction_discount")
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PostAuctionDiscount implements Serializable {

  private static final long serialVersionUID = -6880586620813004204L;

  public PostAuctionDiscount(
      Long pid,
      String discountName,
      String discountDescription,
      Double discountPercent,
      Boolean discountStatus,
      Boolean openAuctionEnabled,
      PostAuctionDealsSelected dealsSelected,
      Integer version,
      List<PostAuctionDiscountSeller> sellers,
      List<PostAuctionDiscountRevenueGroup> revenueGroups,
      List<PostAuctionDiscountDspSeat> dsps,
      List<DealPostAuctionDiscount> deals,
      Date lastUpdate,
      Date creationDate) {
    this(
        pid,
        discountName,
        discountDescription,
        discountPercent,
        discountStatus,
        openAuctionEnabled,
        dealsSelected,
        version,
        lastUpdate,
        creationDate);
    setSellers(sellers);
    setRevenueGroups(revenueGroups);
    setDsps(dsps);
    setDeals(deals);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "discount_name", unique = true)
  @NotNull
  @ToString.Include
  private String discountName;

  @Column(name = "discount_description")
  private String discountDescription;

  @Column(name = "discount_percent")
  @ToString.Include
  private Double discountPercent;

  @Column(name = "discount_status")
  @ToString.Include
  private Boolean discountStatus;

  @Column(name = "open_auction_enabled")
  @ToString.Include
  private Boolean openAuctionEnabled;

  @Enumerated(EnumType.STRING)
  @Column(name = "deals_selected")
  @ToString.Include
  private PostAuctionDealsSelected dealsSelected;

  @Version
  @Column(name = "version", nullable = false)
  @ToString.Include
  private Integer version;

  @OneToMany(
      mappedBy = "postAuctionDiscount",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private final List<PostAuctionDiscountSeller> sellers = new ArrayList<>();

  @OneToMany(
      mappedBy = "postAuctionDiscount",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private final List<PostAuctionDiscountRevenueGroup> revenueGroups = new ArrayList<>();

  @OneToMany(
      mappedBy = "postAuctionDiscount",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private final List<PostAuctionDiscountDspSeat> dsps = new ArrayList<>();

  @OneToMany(
      mappedBy = "postAuctionDiscount",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private final List<DealPostAuctionDiscount> deals = new ArrayList<>();

  @Column(name = "last_update")
  private Date lastUpdate;

  @Column(name = "creation_date")
  private Date creationDate;

  /** Invoked before insert to set the current time. */
  @PrePersist
  @PreUpdate
  public void prePersist() {
    lastUpdate = Date.from(Instant.now());
    creationDate = ObjectUtils.firstNonNull(creationDate, lastUpdate);
  }

  public void setSellers(Collection<PostAuctionDiscountSeller> discountSellers) {
    this.sellers.clear();
    if (discountSellers != null) {
      this.sellers.addAll(discountSellers);
    }
  }

  public void setRevenueGroups(Collection<PostAuctionDiscountRevenueGroup> discountRevenueGroups) {
    this.revenueGroups.clear();
    if (discountRevenueGroups != null) {
      this.revenueGroups.addAll(discountRevenueGroups);
    }
  }

  public void setDsps(Collection<PostAuctionDiscountDspSeat> discountDSPSeats) {
    this.dsps.clear();
    if (discountDSPSeats != null) {
      this.dsps.addAll(discountDSPSeats);
    }
  }

  public void setDeals(Collection<DealPostAuctionDiscount> discountDeals) {
    this.deals.clear();
    if (discountDeals != null) {
      this.deals.addAll(discountDeals);
    }
  }
}
