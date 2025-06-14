package com.ssp.geneva.server.bidinspector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BidderDealDTO {
  private String auctionRunHashId;
  private Integer bidderId;
  private String dealId;
}
