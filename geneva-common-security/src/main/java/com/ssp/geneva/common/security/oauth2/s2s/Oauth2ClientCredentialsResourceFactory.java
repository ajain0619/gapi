package com.ssp.geneva.common.security.oauth2.s2s;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

@Log4j2
public class Oauth2ClientCredentialsResourceFactory {

  private final String oneIDBaseURL;
  private final String realm;
  private List<String> scope;
  private final String clientId;
  private final String clientSecret;
  private static final String ACCESS_TOKEN_URL_PATH = "identity/oauth2/access_token";

  public Oauth2ClientCredentialsResourceFactory(
      final String accessTokenBaseURL, final String realm, String clientId, String clientSecret) {
    this.oneIDBaseURL = accessTokenBaseURL;
    this.realm = realm;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  public ClientCredentialsResourceDetails createClientCredentialsResourceDetails() {
    ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
    resource.setClientId(clientId);
    resource.setClientSecret(clientSecret);
    resource.setScope(getScope());
    resource.setAccessTokenUri(getAccessTokenURL());
    return resource;
  }

  public void setScope(List<String> scope) {
    this.scope = scope;
  }

  public List<String> getScope() {
    return scope;
  }

  private String getAccessTokenURL() {
    StringBuilder sb = new StringBuilder(oneIDBaseURL);
    if (!oneIDBaseURL.endsWith("/")) {
      sb.append("/");
    }
    sb.append(ACCESS_TOKEN_URL_PATH);
    if (isNotBlank(realm)) {
      sb.append("?").append("realm=").append(realm);
    }
    return sb.toString();
  }
}
