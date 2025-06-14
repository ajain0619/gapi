package com.nexage.admin.core.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Audited
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("seller_seat")
public class SellerSeatMdmId extends MdmId {

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @Getter
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_seat_pid")
  private SellerSeat sellerSeat;
}
