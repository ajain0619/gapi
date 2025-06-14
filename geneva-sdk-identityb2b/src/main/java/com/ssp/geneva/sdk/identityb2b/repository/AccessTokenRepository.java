package com.ssp.geneva.sdk.identityb2b.repository;

import static com.ssp.geneva.sdk.identityb2b.model.IdentityB2bSdkResourcePath.ACCESS_TOKEN_URL_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.identityb2b.config.IdentityB2bSdkConfigProperties;
import com.ssp.geneva.sdk.identityb2b.model.B2bAccessToken;
import com.ssp.geneva.sdk.identityb2b.util.BuildUri;
import com.ssp.geneva.sdk.identityb2b.util.JwtGenerator;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class AccessTokenRepository extends BaseRestRepository {

  private static final String GRANT_TYPE = "grant_type";
  private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
  private static final String REFRESH_TOKEN = "refresh_token";
  private static final String CLIENT_ASSERTION_TYPE = "client_assertion_type";
  private static final String CLIENT_ASSERTION_TYPE_VALUE =
      "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
  private static final String CLIENT_ASSERTION = "client_assertion";
  private static final String REALM_PARAM = "realm";

  private final String realm;

  private final String clientId;

  private final String clientSecret;

  public AccessTokenRepository(
      final ObjectMapper identityB2bObjectMapper,
      final RestTemplate restTemplate,
      final IdentityB2bSdkConfigProperties identityB2bSdkConfigProperties) {
    super(identityB2bObjectMapper, restTemplate, identityB2bSdkConfigProperties.getB2bHost());
    this.clientSecret = identityB2bSdkConfigProperties.getClientSecret();
    this.clientId = identityB2bSdkConfigProperties.getClientId();
    this.realm = identityB2bSdkConfigProperties.getRealm();
  }

  /**
   * Get an AccessToken from B2B from a valid RefreshToken
   *
   * @param refreshToken @link AccessTokenRequest grant_type required 'refresh_token' refresh_token
   *     'refresh_token' value from the original access_token request client_assertion_type
   *     'urn:ietf:params:oauth:client-assertion-type:jwt-bearer' client_assertion JWS value (varies
   *     for each client request)
   * @return a valid AccessToken
   */
  public ResponseEntity<B2bAccessToken> getAccessTokenByRefreshToken(String refreshToken) {

    final String url = BuildUri.build(baseUrl, ACCESS_TOKEN_URL_PATH.getResourcePath());
    final String clientAssertion = JwtGenerator.generate(baseUrl, realm, clientId, clientSecret);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add(GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);
    map.add(REFRESH_TOKEN, refreshToken);
    map.add(CLIENT_ASSERTION_TYPE, CLIENT_ASSERTION_TYPE_VALUE);
    map.add(CLIENT_ASSERTION, clientAssertion);
    map.add(REALM_PARAM, realm);
    HttpEntity<MultiValueMap<String, String>> requestEntity =
        new HttpEntity<>(map, buildRequestHeaders());

    return makeRequest(url, HttpMethod.POST, requestEntity, B2bAccessToken.class);
  }
}
