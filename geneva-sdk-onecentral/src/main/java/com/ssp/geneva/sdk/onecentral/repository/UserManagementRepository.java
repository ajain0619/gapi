package com.ssp.geneva.sdk.onecentral.repository;

import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.CREATE_API_USER_URL_PATH;
import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.GET_USERS;
import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.UPDATE_USER_URL_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfigProperties;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserResponseDTO.OneCentralUser;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUsersResponseDTO;
import com.ssp.geneva.sdk.onecentral.util.BuildUri;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** Resource Path: /user-management/v6/users */
@Log4j2
public class UserManagementRepository extends BaseRestRepository {

  private static final String EMAIL_QUERY_PARAM = "{email}";
  private static final String SEARCH_SOURCE_QUERY_PARAM = "{searchSource}";

  private static final String ONE_CENTRAL_SEARCH_SOURCE = "One-Central";

  public UserManagementRepository(
      final ObjectMapper oneCentralObjectMapper,
      final RestTemplate s2sTemplate,
      final OneCentralSdkConfigProperties oneCentralSdkConfigProperties) {
    super(oneCentralObjectMapper, s2sTemplate, oneCentralSdkConfigProperties.getSsoOneApiBaseUrl());
  }

  /**
   * POST: Create an user.
   *
   * @param accessToken {@link String} token string to be used in the Authorization bearer header
   * @param request {@link OneCentralUserRequestDTO}
   * @return {@link ResponseEntity} of type {@link OneCentralUser} with service request response.
   */
  public ResponseEntity<OneCentralUser> createUser(
      String accessToken, OneCentralUserRequestDTO request) {
    var resourcePath = CREATE_API_USER_URL_PATH.getResourcePath();
    String url = BuildUri.build(this.baseUrl, resourcePath);
    HttpEntity<OneCentralUserRequestDTO> requestEntity =
        new HttpEntity<>(request, buildRequestHeaders(accessToken));
    log.debug("The url used to create a user in oneCentral is : {}", url);
    return makeRequest(url, HttpMethod.POST, requestEntity, OneCentralUser.class);
  }

  /**
   * GET: Get one central users by email.
   *
   * @param accessToken {@link String} token string to be used in the Authorization bearer header
   * @param email {@link String}
   * @return {@link ResponseEntity} of type {@link List} with service request response.
   */
  public ResponseEntity<OneCentralUsersResponseDTO> getUsersByEmail(
      String accessToken, String email) {
    var resourcePath = GET_USERS.getResourcePath();
    String url = BuildUri.build(this.baseUrl, resourcePath);

    Map<String, String> params =
        Map.of(
            "email", email,
            "searchSource", ONE_CENTRAL_SEARCH_SOURCE);

    URI uri =
        UriComponentsBuilder.fromUriString(url)
            .queryParam("email", EMAIL_QUERY_PARAM)
            .queryParam("searchSource", SEARCH_SOURCE_QUERY_PARAM)
            .build(params);

    HttpEntity<Void> requestEntity = new HttpEntity<>(buildRequestHeaders(accessToken));

    return makeRequest(uri, HttpMethod.GET, requestEntity, OneCentralUsersResponseDTO.class);
  }

  /**
   * PUT: Update an user.
   *
   * @param accessToken {@link String} token string to be used in the Authorization bearer header
   * @param request {@link OneCentralUserRequestDTO}
   * @return {@link ResponseEntity} of type {@link OneCentralUser} with service request response.
   */
  public ResponseEntity<OneCentralUser> updateUser(
      String accessToken, OneCentralUserRequestDTO request) {
    var resourcePath = UPDATE_USER_URL_PATH.getResourcePath();
    Map<String, String> pathVars = Map.of("username", request.getUsername());
    String url = BuildUri.build(this.baseUrl, resourcePath, pathVars);
    log.debug("The url used to update a user in oneCentral is : {}", url);
    HttpEntity<OneCentralUserRequestDTO> requestEntity =
        new HttpEntity<>(request, buildRequestHeaders(accessToken));
    return makeRequest(url, HttpMethod.PUT, requestEntity, OneCentralUser.class);
  }
}
