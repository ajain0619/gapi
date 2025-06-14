package com.ssp.geneva.sdk.ckms.config;

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
public class CkmsSdkConfigProperties {
  private final Boolean mockYKeyKey;
  private final String certPath;
  private final String trustStorePath;
  private final String keyPath;
  private final String yKeyKeyEnvironment;
  private final String defaultKeyGroups;
  @ToString.Exclude private final String trustStorePassword;
  private final String connectionRetryCount;
  private final String connectionTimeout;
}
