package com.ssp.geneva.sdk.xandr.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
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
public class BuyerSeat implements Serializable {
  @JsonProperty("bidder_id")
  private int bidderId;

  @JsonProperty("bidder_name")
  private String bidderName;

  private String code;
  @EqualsAndHashCode.Include private String name;
}
