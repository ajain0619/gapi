package com.ssp.geneva.sdk.dv360.seller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.sdk.dv360.seller.model.type.PricingType;
import com.ssp.geneva.sdk.dv360.seller.model.type.TransactionType;
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
public class Product implements Dv360NamedRequest {
  @EqualsAndHashCode.Include private String name;
  private String displayName;
  private String externalDealId;
  private TransactionType transactionType;
  private PricingType pricingType;
  private String updateTime;
  private String startTime;
  private String endTime;
  private RateDetails rateDetails;
  private List<CreativeConfig> creativeConfig;
}
