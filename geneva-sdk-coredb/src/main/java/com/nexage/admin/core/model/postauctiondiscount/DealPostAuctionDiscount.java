package com.nexage.admin.core.model.postauctiondiscount;

import com.nexage.admin.core.model.DirectDealView;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.envers.Audited;

@Entity
@Table(
    name = "deal_post_auction_discount",
    uniqueConstraints = @UniqueConstraint(columnNames = {"post_auction_discount_pid", "deal_pid"}))
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DealPostAuctionDiscount implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_auction_discount_pid", referencedColumnName = "pid")
  private PostAuctionDiscount postAuctionDiscount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_pid", referencedColumnName = "pid")
  private DirectDealView deal;

  @Column(name = "updated_on")
  private Date updatedOn;

  @Column(name = "created_on")
  private Date createdOn;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  /** Invoked before insert to set the current time. */
  @PrePersist
  @PreUpdate
  public void prePersist() {
    updatedOn = Date.from(Instant.now());
    createdOn = ObjectUtils.firstNonNull(createdOn, updatedOn);
  }
}
