package com.ssp.geneva.common.security.oauth2.oidc;

import org.springframework.beans.factory.annotation.Value;

public class OpenIdFileSystemCredentialsConfig implements OpenIdCredentialsConfigHandler {

  @Value("${geneva.sso.oidc.client.id}")
  private String clientId;

  @Value("${geneva.sso.oidc.client.secret}")
  private String clientSecret;

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }
}
