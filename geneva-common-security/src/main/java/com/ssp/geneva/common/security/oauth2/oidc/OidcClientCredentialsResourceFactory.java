package com.ssp.geneva.common.security.oauth2.oidc;

import static org.springframework.security.oauth2.common.AuthenticationScheme.header;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

public class OidcClientCredentialsResourceFactory {

  private static final String RESOURCE_ID = "oidcResource";
  private static final AuthenticationScheme AUTHENTICATION_SCHEME = header;
  private static final AuthenticationScheme CLIENT_AUTHENTICATION_SCHEME = header;
  private final OpenIdCredentialsConfigHandler configHandler;
  private final String baseUrl;
  private final String realm;
  private final String baseApplicationUri;
  private static final String USER_AUTHORIZATION_URL_PATH = "identity/oauth2/authorize";
  private static final String ACCESS_TOKEN_URL_PATH = "identity/oauth2/access_token";
  private static final String SSO_URL_PATH = "geneva/sso";

  @Value("${sso.oneId.scope}")
  private List<String> scope;

  public OidcClientCredentialsResourceFactory(
      OpenIdCredentialsConfigHandler openIdCredentialsConfigHandler,
      final String accessTokenBaseURL,
      final String realm,
      final String baseApplicationUri) {
    this.configHandler = openIdCredentialsConfigHandler;
    this.baseUrl = accessTokenBaseURL;
    this.realm = realm;
    this.baseApplicationUri = baseApplicationUri;
  }

  public AuthorizationCodeResourceDetails createAuthorizationCodeResourceDetails() {
    AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
    resource.setId(RESOURCE_ID);
    resource.setClientId(configHandler.getClientId());
    resource.setClientSecret(configHandler.getClientSecret());
    resource.setUserAuthorizationUri(makeUrl(baseUrl, USER_AUTHORIZATION_URL_PATH));
    resource.setAccessTokenUri(getAccessTokenURL());
    resource.setAuthenticationScheme(AUTHENTICATION_SCHEME);
    resource.setClientAuthenticationScheme(CLIENT_AUTHENTICATION_SCHEME);
    resource.setScope(scope);
    resource.setPreEstablishedRedirectUri(makeUrl(getBaseApplicationUri(), SSO_URL_PATH));
    resource.setUseCurrentUri(false);
    return resource;
  }

  private String makeUrl(final String baseUrl, final String path) {
    StringBuilder sb = new StringBuilder(baseUrl);
    if (!baseUrl.endsWith("/")) {
      sb.append("/");
    }
    sb.append(path);
    return sb.toString();
  }

  private String getAccessTokenURL() {
    StringBuilder sb = new StringBuilder(baseUrl);
    if (!baseUrl.endsWith("/")) {
      sb.append("/");
    }
    sb.append(ACCESS_TOKEN_URL_PATH);
    if (StringUtils.isNotBlank(realm)) {
      sb.append("?").append("realm=").append(realm);
    }
    return sb.toString();
  }

  public String getBaseApplicationUri() {
    return baseApplicationUri;
  }
}
