package com.nexage.geneva.model.crud;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FactCowboyExchangeDeal {
  private String start;
  private String auctionRunHashId;
  private String auctionRunId;
  private Integer bidderId;
  private String dealId;
}
