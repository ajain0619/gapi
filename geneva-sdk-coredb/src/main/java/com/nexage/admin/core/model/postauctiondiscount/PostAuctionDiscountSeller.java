package com.nexage.admin.core.model.postauctiondiscount;

import com.nexage.admin.core.model.CompanyView;
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
    name = "post_auction_discount_seller",
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"post_auction_discount_pid", "company_pid"}))
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PostAuctionDiscountSeller implements Serializable {

  private static final long serialVersionUID = -7212845220797239223L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_auction_discount_pid", referencedColumnName = "pid")
  private PostAuctionDiscount postAuctionDiscount;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  private CompanyView seller;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "type_pid")
  private PostAuctionDiscountType type;

  @Column(name = "last_update")
  private Date lastUpdate;

  @Column(name = "creation_date")
  private Date creationDate;

  @Version
  @Column(name = "version", nullable = false)
  @ToString.Include
  private Integer version;

  /** Invoked before insert to set the current time. */
  @PrePersist
  @PreUpdate
  public void prePersist() {
    lastUpdate = Date.from(Instant.now());
    creationDate = ObjectUtils.firstNonNull(creationDate, lastUpdate);
  }
}
