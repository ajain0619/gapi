package com.nexage.admin.core.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

@Entity
@Audited
@DiscriminatorValue("SELLER_SEAT")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SellerSeatRule extends Rule {

  private static final long serialVersionUID = 1L;

  @Column(name = "seller_seat_pid")
  @NotNull
  private Long sellerSeatPid;
}
