package com.nexage.app.services.impl.segments.cache;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class AllowedMdmIdsForSellerSeat implements Serializable {

  private long sellerSeatPid;
  private int[] mdmIds;
}
