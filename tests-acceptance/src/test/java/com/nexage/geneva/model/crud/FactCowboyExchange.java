package com.nexage.geneva.model.crud;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FactCowboyExchange {
  private String start;
  private String auctionRunHashId;
  private String auctionRunId;
  private Integer bidderId;
  private String bidderUrl;
  private Integer responseCode;
  private String requestPayload;
  private String responsePayload;
  private Integer prebidFilterReason;
}
