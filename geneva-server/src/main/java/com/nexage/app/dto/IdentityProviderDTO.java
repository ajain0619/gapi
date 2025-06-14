package com.nexage.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdentityProviderDTO {
  private Long pid;
  private Integer version;
  private String name;
  private String displayName;
  private Integer providerId;
  private String domain;
  private Boolean enabled;
  private Boolean uiVisible;
}
