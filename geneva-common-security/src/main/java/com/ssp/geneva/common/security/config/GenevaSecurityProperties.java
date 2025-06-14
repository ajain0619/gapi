package com.ssp.geneva.common.security.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenevaSecurityProperties {

  @NonNull private String ssoOneIdBaseUrl;
  @NonNull private String ssoOneIdRealm;
  @NonNull private String ssoOneIdBaseApplicationUri;
  private String ssoUiBaseEndpoint;
  private boolean genevaServerTestingUserEnabled = false;
  private Integer springSessionMaxInactiveTimeout = 1440;
  private String genevaSsoClientId;
  private String genevaSsoClientSecret;
}
