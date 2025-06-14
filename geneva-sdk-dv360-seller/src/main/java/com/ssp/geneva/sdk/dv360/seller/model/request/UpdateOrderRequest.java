package com.ssp.geneva.sdk.dv360.seller.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.sdk.dv360.seller.model.Dv360Request;
import com.ssp.geneva.sdk.dv360.seller.model.Order;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@JsonInclude(Include.NON_NULL)
public class UpdateOrderRequest implements Dv360Request {
  @EqualsAndHashCode.Include private Order order;
  @EqualsAndHashCode.Include private String updateMask;
}
