package com.nexage.app.dto.deal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class DealBidderDTO {

  List<String> wseat;
  List<String> adomains;
  private Long pid;
  private Long bidderPid;

  public DealBidderDTO() {}

  private DealBidderDTO(Builder builder) {
    this.pid = builder.pid;
    this.bidderPid = builder.bidderPid;
    this.wseat = builder.wseat;
    this.adomains = builder.adomains;
  }

  public Long getPid() {
    return pid;
  }

  public Long getBidderPid() {
    return bidderPid;
  }

  public List<String> getWseat() {
    return wseat;
  }

  public List<String> getAdomains() {
    return adomains;
  }

  public static final class Builder {

    private Long pid;
    private Long bidderPid;
    private List<String> wseat;
    private List<String> adomains;

    public Builder setPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder setBidderPid(Long pid) {
      this.bidderPid = pid;
      return this;
    }

    public Builder setFilterSeats(List<String> seats) {
      this.wseat = seats;
      return this;
    }

    public Builder setFilterAdomains(List<String> domains) {
      this.adomains = domains;
      return this;
    }

    public DealBidderDTO build() {
      return new DealBidderDTO(this);
    }
  }
}
