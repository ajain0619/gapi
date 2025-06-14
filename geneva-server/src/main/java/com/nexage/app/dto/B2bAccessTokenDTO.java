package com.nexage.app.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class B2bAccessTokenDTO {
  @EqualsAndHashCode.Include @ToString.Include String access_token;
  @EqualsAndHashCode.Include @ToString.Include String scope;
  @EqualsAndHashCode.Include @ToString.Include String id_token;
  @EqualsAndHashCode.Include @ToString.Include String token_type;
  @EqualsAndHashCode.Include @ToString.Include String expires_in;
}
