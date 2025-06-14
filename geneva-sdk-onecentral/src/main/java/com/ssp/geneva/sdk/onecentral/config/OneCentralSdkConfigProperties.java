package com.ssp.geneva.sdk.onecentral.config;

import com.ssp.geneva.sdk.onecentral.model.Role;
import java.util.Set;
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
public class OneCentralSdkConfigProperties {
  private final Boolean ssoCreateOneCentralUser;
  private final String ssoOneApiBaseUrl;
  private final String ssoUiBaseEndpoint;
  private final String ssoSystemName;
  private final String ssoRoleId; // TODO: To be moved to configuration file
  private final String ssoApiUserRoleId; // TODO: To be moved to configuration file
  private final String environment;
  private Set<Role> roles;
}
