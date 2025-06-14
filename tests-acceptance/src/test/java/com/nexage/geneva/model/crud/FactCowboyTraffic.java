package com.nexage.geneva.model.crud;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FactCowboyTraffic {
  private String start;
  private String auctionRunHashId;
  private String auctionRunId;
  private Long sellerId;
  private Long siteId;
  private Integer placementId;
  private String dealId;
  private String seatId;
  private Integer appBundleId;
  private Integer bidderId;
  private Integer hbPartnerPid;
  private String requestUrl;
  private String requestPayload;
  private String responsePayload;
}
