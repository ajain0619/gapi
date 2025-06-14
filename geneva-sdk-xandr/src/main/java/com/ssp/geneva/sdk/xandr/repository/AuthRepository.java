package com.ssp.geneva.sdk.xandr.repository;

import com.ssp.geneva.sdk.xandr.error.XandrSdkErrorCodes;
import com.ssp.geneva.sdk.xandr.exception.XandrSdkException;
import com.ssp.geneva.sdk.xandr.model.AuthRequest;
import com.ssp.geneva.sdk.xandr.model.AuthResponse;
import com.ssp.geneva.sdk.xandr.model.XandrResponse;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

/**
 * This repository class handles requests made to the Xandr Auth Service:
 * https://docs.xandr.com/bundle/xandr-api/page/04---api-authentication.html
 */
@Log4j2
@EnableScheduling
public class AuthRepository extends BaseRestRepository {

  private static final String AUTH_REST_PATH = "auth";

  private static final String CACHE_MANAGER = "xandrAuthTokenCacheManager";
  private static final String CACHE_NAME = "xandrAuthTokenCache";
  private static final String CACHE_KEY = "authToken";

  private final String xandrCredentials;
  private final String xandrCredentialsMsRebroadcast;

  @Builder
  public AuthRepository(
      String xandrEndpoint,
      RestTemplate xandrRestTemplate,
      String xandrCredentials,
      String xandrCredentialsMsRebroadcast) {
    super(xandrEndpoint, xandrRestTemplate);
    this.xandrCredentials = xandrCredentials;
    this.xandrCredentialsMsRebroadcast = xandrCredentialsMsRebroadcast;
  }

  /**
   * POST credentials to Xandr auth service to obtain a token
   *
   * @param credentials {@link String} json containing username and password
   * @return {@link ResponseEntity <String>} containing an auth token
   */
  @Cacheable(value = CACHE_NAME, key = CACHE_KEY, cacheManager = CACHE_MANAGER)
  public String getAuthToken(String credentials) {
    try {
      log.debug("AuthRepository.getAuthToken()");
      JSONParser parser = new JSONParser();
      JSONObject credJson = (JSONObject) parser.parse(credentials);
      AuthRequest authRequest = AuthRequest.builder().auth(credJson).build();
      RequestEntity<AuthRequest> request =
          generateRequest(AUTH_REST_PATH, HttpMethod.POST, authRequest);
      ResponseEntity<AuthResponse> response =
          sendRequest(request, AuthRequest.class, AuthResponse.class);
      return Optional.ofNullable(response)
          .map(ResponseEntity::getBody)
          .map(AuthResponse::getResponse)
          .map(XandrResponse::getContent)
          .orElse(null);
    } catch (ParseException ex) {
      log.error("Error parsing Xandr credentials: {}", ex);
      throw new XandrSdkException(
          XandrSdkErrorCodes.XANDR_SDK_CREDENTIALS_ERROR, new String[] {ex.getMessage()});
    }
  }

  /**
   * Generate a set of {@link HttpHeaders} including an access token for the Xandr user account
   *
   * @return {@link HttpHeaders>} A newly generated set of {@link HttpHeaders}
   */
  public HttpHeaders getAuthHeaderForXandr() {
    return getAuthHeader(xandrCredentials);
  }

  /**
   * Generate a set of {@link HttpHeaders} including an access token for the Xandr Microsoft
   * Rebroadcasting user account
   *
   * @return {@link HttpHeaders>} A newly generated set of {@link HttpHeaders}
   */
  public HttpHeaders getAuthHeaderForXandrMsRebroadcast() {
    return getAuthHeader(xandrCredentialsMsRebroadcast);
  }

  /**
   * Generate a set of {@link HttpHeaders} including an access token for the Xandr Service
   *
   * @param credentials {@link String} json containing username and password
   * @return {@link HttpHeaders>} A newly generated set of {@link HttpHeaders}
   */
  public HttpHeaders getAuthHeader(String credentials) {
    HttpHeaders httpHeaders = new HttpHeaders();
    String authToken = Optional.ofNullable(getAuthToken(credentials)).orElse(null);
    if (authToken != null) {
      log.debug("access token length: {}", authToken.length());
    }
    httpHeaders.add("Authorization", authToken);
    return httpHeaders;
  }

  @CacheEvict(allEntries = true, value = CACHE_NAME, cacheManager = CACHE_MANAGER)
  @Scheduled(fixedDelayString = "${xandr.sdk.auth.cache.expiration.ms}")
  public void cacheEvict() {
    /* Empty method to allow for @CacheEvict and @Scheduled annotations
    to be used for periodically clearing the credentials cache */
  }
}
