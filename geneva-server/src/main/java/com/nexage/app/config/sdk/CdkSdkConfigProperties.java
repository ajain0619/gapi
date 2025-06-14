package com.nexage.app.config.sdk;

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
public class CdkSdkConfigProperties {
  private final Boolean ckmsEnabled;
  private final String crsSsoClientId;
  private final Boolean crsSsoSecretEncrypted;
  @ToString.Exclude private final String crsSsoSecret;
  private final String genevaMetricsJmxDomain;
  @ToString.Exclude private final String crsSsoCkmsKeyClient;
  @ToString.Exclude private final String crsSsoCkmsKeySecret;
  private final String crsSsoCkmsKeyGroup;
}
