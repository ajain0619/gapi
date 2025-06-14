package com.nexage.app.dto.buyer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@Getter
@Builder
public class BuyerTrafficConfigDTO {

  public enum AuctionType {
    ALL,
    FIRST_PRICE,
    SECOND_PRICE;
  }

  public enum BidderFormat {
    OpenRTBv2,
    OpenRTBv2_2,
    OpenRTBv2_3,
    OpenRTBv2_3_1,
    OpenRTBv2_4,
    OpenRTBv2_4_1,
    OpenRTBv2_4_2,
    OpenRTBv2_5,
    OpenRTBv2_5_1;
  }

  private Long pid;
  private Integer version;
  private Boolean trafficEnabled;
  private Integer maximumQps; // -1 unlimited

  private AuctionType auctionTypes;
  private Map<String, String> countryFilters;
  private Boolean countryAllowlist;
  private Set<String> categoryFilters;
  private Boolean categoryAllowlist;
  private List<String> publisherFilters;
  private Boolean publisherAllowlist;
  private List<String> siteFilters;
  private Boolean siteAllowlist;
  private Boolean locationRequired;
  private Boolean deviceIdRequired;
  private BidderFormat bidderFormat;
  @Singular private Set<BuyerRegionLimitDTO> regionLimits;
  @Singular private Set<BuyerSubscriptionDTO> subscriptions;
  private List<String> allowedTraffic;
}
