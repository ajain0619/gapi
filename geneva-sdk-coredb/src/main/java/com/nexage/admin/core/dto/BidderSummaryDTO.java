package com.nexage.admin.core.dto;

import java.io.Serializable;

public class BidderSummaryDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private final long pid;
  private final String name;
  private final long buyerPid;

  public BidderSummaryDTO(long pid, String name) {
    this(pid, name, 0);
  }

  public BidderSummaryDTO(long pid, String name, long buyerPid) {
    this.pid = pid;
    this.name = name;
    this.buyerPid = buyerPid;
  }

  public long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public long getBuyerPid() {
    return buyerPid;
  }

  @Override
  public String toString() {
    return "BidderSummary [pid="
        + pid
        + ", name="
        + name
        + (buyerPid == 0 ? "" : ", buyerPid=" + buyerPid)
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + (int) (pid ^ (pid >>> 32));
    result = prime * result + (int) (buyerPid ^ (buyerPid >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BidderSummaryDTO other = (BidderSummaryDTO) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (pid != other.pid) return false;
    if (buyerPid != other.buyerPid) return false;
    return true;
  }
}
