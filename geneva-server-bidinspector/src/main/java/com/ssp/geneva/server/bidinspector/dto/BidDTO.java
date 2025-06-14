package com.ssp.geneva.server.bidinspector.dto;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BidDTO {

  private String auctionRunHashId;
  private String recordTime;
  private Long sellerId;
  private Long siteId;
  private Integer placementId;
  private String dealId;
  private String seatId;
  private BigInteger appBundleId;
  private Integer hbPartnerPid;
  private Integer bidderId;
  private String requestUrl;
  private String requestPayload;
  private String responsePayload;
  private String bidCount;
  private String appBundleName;
}
