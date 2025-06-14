package com.ssp.geneva.sdk.xandr.config;

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
public class XandrSdkConfigProperties {
  @NonNull @NotNull private String xandrEndpoint;
  @NonNull @NotNull private String xandrCredentials;
  @NonNull @NotNull private String xandrCredentialsMsRebroadcast;
}
