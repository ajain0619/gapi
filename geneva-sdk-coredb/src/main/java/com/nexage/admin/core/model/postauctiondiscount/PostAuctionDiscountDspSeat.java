package com.nexage.admin.core.model.postauctiondiscount;

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
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.envers.Audited;

@Entity
@Table(
    name = "post_auction_discount_dsp_seat",
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"post_auction_discount_pid", "dsp_seat_pid"}))
@Audited
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class PostAuctionDiscountDspSeat implements Serializable {

  private static final long serialVersionUID = 7214856194306238569L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_auction_discount_pid", referencedColumnName = "pid")
  private PostAuctionDiscount postAuctionDiscount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dsp_seat_pid", referencedColumnName = "pid")
  private PostAuctionDiscountDspSeatView dsp;

  @Version
  @Column(name = "version", nullable = false)
  @ToString.Include
  private Integer version;

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
}
