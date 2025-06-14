package com.ssp.geneva.sdk.onecentral.repository;

import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.GET_USER_AUTH_URL_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfigProperties;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserAuthResponse;
import com.ssp.geneva.sdk.onecentral.util.BuildUri;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/** Resource Path: /user-authorization/v3/users */
@Log4j2
public class UserAuthorizationRepository extends BaseRestRepository {

  public UserAuthorizationRepository(
      final ObjectMapper oneCentralObjectMapper,
      final RestTemplate s2sTemplate,
      final OneCentralSdkConfigProperties oneCentralSdkConfigProperties) {
    super(oneCentralObjectMapper, s2sTemplate, oneCentralSdkConfigProperties.getSsoOneApiBaseUrl());
  }

  /**
   * GET: Get User Authorization by Token.
   *
   * @param accessToken token of the user
   * @return 1C user authorization response
   */
  public ResponseEntity<OneCentralUserAuthResponse> getUserAuth(String accessToken) {
    var resourcePath = GET_USER_AUTH_URL_PATH.getResourcePath();
    String url = BuildUri.build(this.baseUrl, resourcePath);
    log.debug("Get user authorization from OneCentral: {}", url);

    var requestEntity = new HttpEntity<>(buildRequestHeaders(accessToken));
    try {
      return makeRequest(url, HttpMethod.GET, requestEntity, OneCentralUserAuthResponse.class);
    } catch (OneCentralException e) {
      log.error("Error getting user authorization from OneCentral");
      throw new OneCentralException(
          OneCentralErrorCodes.ONECENTRAL_USER_NOT_FOUND, e.getOneCentralErrorResponse());
    }
  }
}
