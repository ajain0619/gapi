package com.ssp.geneva.sdk.xandr.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssp.geneva.sdk.xandr.annotation.CurrencyCode;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class Deal implements Serializable {
  private int id;
  private String code;
  @EqualsAndHashCode.Include private String name;
  private boolean active;

  @JsonProperty("start_date")
  private String startDate;

  @JsonProperty("end_date")
  private String endDate;

  @CurrencyCode private String currency;

  @JsonProperty("use_deal_floor")
  private boolean useDealFloor;

  @JsonProperty("floor_price")
  private double floorPrice;

  private Seller seller;

  @JsonProperty("buyer_seats")
  private List<BuyerSeat> buyerSeats;

  private Type type;

  @JsonProperty("auction_type")
  private AuctionType auctionType;

  @JsonProperty("ask_price")
  private double askPrice;

  private int version;
}
