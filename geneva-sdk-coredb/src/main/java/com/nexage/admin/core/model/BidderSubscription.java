package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Audited
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Table(name = "bidder_subscription")
@NamedQuery(
    name = "getBidderSubscriptionForDataProvidersAndBidderConfig",
    query =
        "SELECT bs FROM BidderSubscription bs WHERE bs.externalDataProvider.pid = :dataProviderPid and bs.bidderConfig.id = :bidderId")
@NamedQuery(
    name = "getBidderSubscriptionForDataProviders",
    query =
        "SELECT bs FROM BidderSubscription bs WHERE bs.externalDataProvider.pid  = :dataProviderPid")
public class BidderSubscription implements Serializable {

  private static final long serialVersionUID = 661194813330801813L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @ToString.Include
  private Long pid;

  @Column(name = "requires_data_to_bid", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private boolean requiresDataToBid;

  @Column(name = "bidder_alias")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String bidderAlias;

  @Version
  @Column(name = "VERSION", nullable = false)
  @ToString.Include
  private Integer version;

  /** Associated bidder config. */
  @ManyToOne
  @JoinColumn(name = "bidder_pid")
  @JsonBackReference
  @EqualsAndHashCode.Include
  private BidderConfig bidderConfig;

  @NotAudited
  @Column(name = "bidder_id")
  @JsonIgnore
  @ToString.Include
  private String bidderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "data_provider_pid")
  @EqualsAndHashCode.Include
  private ExternalDataProvider externalDataProvider;

  @PrePersist
  @PreUpdate
  private void prePersist() {
    if (bidderConfig != null) {
      this.bidderId = bidderConfig.getId();
    }
  }

  public BidderSubscription(ExternalDataProvider dsp, BidderConfig bc) {
    this.externalDataProvider = dsp;
    this.bidderConfig = bc;
  }
}
