package com.ssp.geneva.sdk.dwdb.config;

import java.util.Properties;
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
public class DwDbSdkConfigProperties {
  private final String poolName;
  private final String jdbcUrl;
  private final String driverClassName;
  private final Integer maximumPoolSize;
  private final Long idleTimeout;
  private final Long connectionTimeout;
  private final Long maxLifetime;
  private final String username;
  private final String password;
  private final String connectionTestQuery;
  private final Properties dataSourceProperties;
}
