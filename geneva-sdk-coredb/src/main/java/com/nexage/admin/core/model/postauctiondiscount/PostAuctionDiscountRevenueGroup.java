package com.nexage.admin.core.model.postauctiondiscount;

import com.nexage.admin.core.model.RevenueGroup;
import java.io.Serial;
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
@Audited
@Table(name = "post_auction_discount_revenue_group")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PostAuctionDiscountRevenueGroup implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_auction_discount_pid")
  private PostAuctionDiscount postAuctionDiscount;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "revenue_group_pid")
  private RevenueGroup revenueGroup;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "type_pid")
  private PostAuctionDiscountType type;

  @Version @ToString.Include private Integer version;

  @Column(name = "updated_on")
  private Date updatedOn;

  @Column(name = "created_on")
  private Date createdOn;

  @PrePersist
  @PreUpdate
  public void setTimestamps() {
    updatedOn = Date.from(Instant.now());
    createdOn = ObjectUtils.firstNonNull(createdOn, updatedOn);
  }
}
