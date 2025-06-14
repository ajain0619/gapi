package com.ssp.geneva.sdk.dv360.seller.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.sdk.dv360.seller.model.Dv360Request;
import com.ssp.geneva.sdk.dv360.seller.model.Product;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@JsonInclude(Include.NON_NULL)
public class UpdateProductRequest implements Dv360Request {
  @EqualsAndHashCode.Include private Product product;
  @EqualsAndHashCode.Include private String updateMask;
}
