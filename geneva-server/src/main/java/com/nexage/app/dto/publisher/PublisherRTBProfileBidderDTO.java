package com.nexage.app.dto.publisher;

import java.util.Set;

public class PublisherRTBProfileBidderDTO {

  private Long pid;
  private Integer version;
  private Long bidderPid;
  /** @deprecated use the inclusive term {@link #seatAllowlist} instead. */
  @Deprecated private Set<String> seatWhitelist;

  private Set<String> seatAllowlist;

  public PublisherRTBProfileBidderDTO() {}

  public PublisherRTBProfileBidderDTO(
      Long pid,
      Integer version,
      Long bidderPid,
      Set<String> seatWhitelist,
      Set<String> seatAllowlist) {
    this.pid = pid;
    this.version = version;
    this.bidderPid = bidderPid;
    this.seatWhitelist = seatWhitelist;
    this.seatAllowlist = seatAllowlist;
  }

  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Long getBidderPid() {
    return bidderPid;
  }

  public void setBidderPid(Long bidderPid) {
    this.bidderPid = bidderPid;
  }

  public Set<String> getSeatWhitelist() {
    if (null == seatAllowlist) {
      return seatWhitelist;
    } else {
      return seatAllowlist;
    }
  }

  public void setSeatWhitelist(Set<String> seatWhitelist) {
    if (null != seatAllowlist) {
      this.seatWhitelist = seatAllowlist;
    } else {
      this.seatWhitelist = seatWhitelist;
    }
  }

  public void setSeatAllowlist(Set<String> seatAllowlist) {
    if (null == seatAllowlist && null != seatWhitelist) {
      this.seatAllowlist = seatWhitelist;
    } else {
      this.seatAllowlist = seatAllowlist;
      this.setSeatWhitelist(seatAllowlist);
    }
  }

  public Set<String> getSeatAllowlist() {
    if (null != seatAllowlist) {
      return seatAllowlist;
    } else {
      return seatWhitelist;
    }
  }
}
