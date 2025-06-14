package com.nexage.geneva.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OIDCTokenRequestProperties {

  @Value("${oidc.realm}")
  private String oidcRealm;

  @Value("${oidc.clientId}")
  private String oidcClientId;

  @Value("${oidc.redirectUri}")
  private String oidcRedirectUri;

  @Value("${oidc.ssoCookieName}")
  private String oidcSSOCookieName;

  @Value("${oidc.opUrl}")
  private String oidcOpUrl;

  @Value("${oidc.username}")
  private String oidcUserName;

  @Value("${oidc.password}")
  private String oidcPassword;

  @Value("${oidc.onecentral.username}")
  private String oidcOnecentralUsername;

  public String getRealm() {
    return oidcRealm;
  }

  public String getClientId() {
    return oidcClientId;
  }

  public String getRedirectUri() {
    return oidcRedirectUri;
  }

  public String getSSOCookieName() {
    return oidcSSOCookieName;
  }

  public String getOpUrl() {
    return oidcOpUrl;
  }

  public String getUserName() {
    return oidcUserName;
  }

  public String getPassword() {
    return oidcPassword;
  }

  public String getOnecentralUsername() {
    return oidcOnecentralUsername;
  }
}
