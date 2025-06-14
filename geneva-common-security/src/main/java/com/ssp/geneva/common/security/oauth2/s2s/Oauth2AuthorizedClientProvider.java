package com.ssp.geneva.common.security.oauth2.s2s;

import com.ssp.geneva.common.security.config.configurer.Customization;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.stereotype.Component;

@Component
public class Oauth2AuthorizedClientProvider {

  @Value("${sso.oneId.realm}")
  private String ssoOneIdRealm;

  private final Set<OAuth2AuthorizedClientManager> oAuth2AuthorizedClientManagers;

  public Oauth2AuthorizedClientProvider(
      Set<OAuth2AuthorizedClientManager> oAuth2AuthorizedClientManagers) {
    this.oAuth2AuthorizedClientManagers = oAuth2AuthorizedClientManagers;
  }

  private DefaultClientCredentialsTokenResponseClient createResponseClient() {
    var defaultClientCredentialsTokenResponseClient =
        new DefaultClientCredentialsTokenResponseClient();
    Customization.clientCredentialsTokenResponseClientCustomizerCustomizer(ssoOneIdRealm)
        .customize(defaultClientCredentialsTokenResponseClient);
    return defaultClientCredentialsTokenResponseClient;
  }

  public OAuth2AuthorizedClient getAuthorizedClient() {
    AuthorizedClientServiceOAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager =
        findClientManger(AuthorizedClientServiceOAuth2AuthorizedClientManager.class);

    var auth2AuthorizedClientProvider =
        OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials(
                clientCredentialsProvider ->
                    clientCredentialsProvider.accessTokenResponseClient(createResponseClient()))
            .build();
    defaultOAuth2AuthorizedClientManager.setAuthorizedClientProvider(auth2AuthorizedClientProvider);
    return defaultOAuth2AuthorizedClientManager.authorize(getAuthorizeRequest());
  }

  private <T extends OAuth2AuthorizedClientManager> T findClientManger(Class<T> type) {
    return (T)
        oAuth2AuthorizedClientManagers.stream()
            .filter(manager -> type.isAssignableFrom(manager.getClass()))
            .findFirst()
            .orElseThrow();
  }

  private OAuth2AuthorizeRequest getAuthorizeRequest() {
    return OAuth2AuthorizeRequest.withClientRegistrationId("s2s").principal("s2sUser").build();
  }
}
