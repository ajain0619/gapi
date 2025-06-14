package com.ssp.geneva.sdk.dv360.seller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.sdk.dv360.seller.model.type.FormatType;
import com.ssp.geneva.sdk.dv360.seller.model.type.MediumType;
import com.ssp.geneva.sdk.dv360.seller.model.type.StatusType;
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
public class AuctionPackage implements Dv360NamedRequest {
  @EqualsAndHashCode.Include private String name;
  private String displayName;
  private StatusType status;
  private String description;
  private FormatType format;
  private String externalDealId;
  private String logoUrl;
  private Money floorPrice;
  private String startTime;
  private String endTime;
  private MediumType mediumType;
}
