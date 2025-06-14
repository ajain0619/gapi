package com.nexage.app.dto.deal;

public class DealBuyerDTO {

  private final Long companyPid;
  private final Long bidderPid;
  private final String name;

  public DealBuyerDTO(Long companyPid, Long bidderPid, String name) {
    this.companyPid = companyPid;
    this.bidderPid = bidderPid;
    this.name = name;
  }

  public Long getCompanyPid() {
    return companyPid;
  }

  public Long getBidderPid() {
    return bidderPid;
  }

  public String getName() {
    return name;
  }
}
