package com.ssp.geneva.sdk.dv360.seller.config;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Dv360SellerSdkConfigProperties {
  @NonNull @NotNull private String dv360Endpoint;
  @NonNull @NotNull private String dv360ExchangeId;
  @NonNull @NotNull private String dv360Credentials;
}
