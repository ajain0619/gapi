package com.ssp.geneva.sdk.identityb2b.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccessTokenRequest {

  @EqualsAndHashCode.Include @NonNull @NotNull String grant_type;
  @EqualsAndHashCode.Include @NonNull @NotNull String refresh_token;
  @EqualsAndHashCode.Include @NonNull @NotNull String realm;
  @EqualsAndHashCode.Include @NonNull @NotNull String client_assertion_type;
  @EqualsAndHashCode.Include @NonNull @NotNull String client_assertion;
}
