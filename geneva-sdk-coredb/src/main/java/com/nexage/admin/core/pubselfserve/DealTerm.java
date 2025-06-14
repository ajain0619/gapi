package com.nexage.admin.core.pubselfserve;

import java.io.Serializable;
import java.math.BigDecimal;

public class DealTerm implements Serializable {

  private final BigDecimal revShare;
  private final BigDecimal rtbFee;

  public DealTerm(BigDecimal revShare, BigDecimal rtbFee) {
    this.revShare = revShare;
    this.rtbFee = rtbFee;
  }

  public BigDecimal getRevShare() {
    return revShare;
  }

  public BigDecimal getRtbFee() {
    return rtbFee;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DealTerm dealTerm = (DealTerm) o;

    if (revShare != null ? !revShare.equals(dealTerm.revShare) : dealTerm.revShare != null)
      return false;
    return !(rtbFee != null ? !rtbFee.equals(dealTerm.rtbFee) : dealTerm.rtbFee != null);
  }

  @Override
  public int hashCode() {
    int result = revShare != null ? revShare.hashCode() : 0;
    result = 31 * result + (rtbFee != null ? rtbFee.hashCode() : 0);
    return result;
  }
}
