package com.ssp.geneva.sdk.identityb2b.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class B2bAccessToken {
  @EqualsAndHashCode.Include @ToString.Include String access_token;
  @EqualsAndHashCode.Include @ToString.Include String scope;
  @EqualsAndHashCode.Include @ToString.Include String id_token;
  @EqualsAndHashCode.Include @ToString.Include String token_type;
  @EqualsAndHashCode.Include @ToString.Include String expires_in;
}
