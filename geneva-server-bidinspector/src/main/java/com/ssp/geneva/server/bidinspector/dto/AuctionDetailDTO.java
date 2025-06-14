package com.ssp.geneva.server.bidinspector.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionDetailDTO {
  private Integer id;
  private String auctionRunHashId;
  private Integer bidderId;
  private String bidderUrl;
  private String requestPayload;
  private String responsePayload;
  private Integer responseCode;
  private List<String> dealIds;
}
