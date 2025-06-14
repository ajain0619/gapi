package com.nexage.app.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class AccessTokenDTO {
  @EqualsAndHashCode.Include @ToString.Include String accessToken;
  @EqualsAndHashCode.Include @ToString.Include String tokenType;
  @EqualsAndHashCode.Include @ToString.Include Long expiresIn;
}
