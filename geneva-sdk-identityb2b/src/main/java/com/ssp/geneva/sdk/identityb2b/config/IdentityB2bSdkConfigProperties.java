package com.ssp.geneva.sdk.identityb2b.config;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class to store all key values associated to taxonomy.api properties. The idea behind is to work
 * later as a bucket for spring-boot auto-configuration properties.
 *
 * @see org.springframework.boot.context.properties.ConfigurationProperties;
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class IdentityB2bSdkConfigProperties {
  private String realm;

  private String clientId;

  private String clientSecret;

  private String b2bHost;
}
