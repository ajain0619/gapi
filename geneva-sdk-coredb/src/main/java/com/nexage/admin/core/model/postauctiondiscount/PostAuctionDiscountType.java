package com.nexage.admin.core.model.postauctiondiscount;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
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
@Table(name = "post_auction_discount_type")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PostAuctionDiscountType implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @ToString.Include private String name;

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
