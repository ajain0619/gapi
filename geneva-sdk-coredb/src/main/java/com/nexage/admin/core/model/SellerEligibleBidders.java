package com.nexage.admin.core.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@DiscriminatorValue("PUB")
public class SellerEligibleBidders extends BaseEligibleBidders {
  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "reference_id", referencedColumnName = "pid")
  @EqualsAndHashCode.Include
  private Company publisher;
}
