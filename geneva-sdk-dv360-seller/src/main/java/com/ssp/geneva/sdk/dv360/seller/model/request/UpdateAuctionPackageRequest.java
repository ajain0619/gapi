package com.ssp.geneva.sdk.dv360.seller.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.sdk.dv360.seller.model.AuctionPackage;
import com.ssp.geneva.sdk.dv360.seller.model.Dv360Request;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@JsonInclude(Include.NON_NULL)
public class UpdateAuctionPackageRequest implements Dv360Request {
  @EqualsAndHashCode.Include private AuctionPackage auctionPackage;
  @EqualsAndHashCode.Include private String updateMask;
}
