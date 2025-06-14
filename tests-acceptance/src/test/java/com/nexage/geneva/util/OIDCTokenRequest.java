package com.nexage.geneva.util;

import com.aol.identity.b2b.node.oidc.OIDCNodeAdapter;
import com.aol.identity.b2b.node.oidc.OIDCOptions;
import com.aol.identity.b2b.node.oidc.OIDCTokens;
import com.nexage.geneva.config.properties.OIDCTokenRequestProperties;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OIDCTokenRequest {

  @Autowired private OIDCTokenRequestProperties oidcTokenRequestProperties;

  public String getToken() {
    String nodeModuleRoot =
        String.format("src%1$stest%1$sresources%1$snode-oidc-cli", File.separator);
    String accessToken = null;

    try {

      OIDCNodeAdapter oidcNodeAdapter = new OIDCNodeAdapter(nodeModuleRoot);
      OIDCOptions options =
          new OIDCOptions()
              .realm(oidcTokenRequestProperties.getRealm())
              .clientId(oidcTokenRequestProperties.getClientId())
              .redirectUri(oidcTokenRequestProperties.getRedirectUri())
              .ssoCookieName(oidcTokenRequestProperties.getSSOCookieName())
              .opUrl(oidcTokenRequestProperties.getOpUrl());

      OIDCTokens result =
          oidcNodeAdapter.execute(
              options,
              oidcTokenRequestProperties.getUserName(),
              oidcTokenRequestProperties.getPassword());
      accessToken = result.getAccessToken();
      System.out.println("token: " + accessToken);
    } catch (Exception e) {
      throw new RuntimeException("Token retreival failed" + e.getMessage());
    }
    return accessToken;
  }
}
