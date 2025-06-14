package com.ssp.geneva.sdk.onecentral.repository;

import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.CREATE_SSP_USER_URL_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfigProperties;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserResponseDTO.OneCentralUser;
import com.ssp.geneva.sdk.onecentral.model.UserMigrationRequest;
import com.ssp.geneva.sdk.onecentral.util.BuildUri;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/** Resource Path: /user-migration/v1/users */
@Log4j2
public class UserMigrationRepository extends BaseRestRepository {

  public UserMigrationRepository(
      final ObjectMapper oneCentralObjectMapper,
      final RestTemplate s2sTemplate,
      final OneCentralSdkConfigProperties oneCentralSdkConfigProperties) {
    super(oneCentralObjectMapper, s2sTemplate, oneCentralSdkConfigProperties.getSsoOneApiBaseUrl());
  }

  /**
   * POST: Create an user.
   *
   * @param request {@link UserMigrationRequest}
   * @return {@link ResponseEntity} of type {@link OneCentralUser} with service request response.
   */
  public ResponseEntity<OneCentralUser> create(
      String accessToken, @Valid UserMigrationRequest request) {
    log.debug("request={}", request);
    var resourcePath = CREATE_SSP_USER_URL_PATH.getResourcePath();
    String url = BuildUri.build(this.baseUrl, resourcePath);
    HttpEntity<UserMigrationRequest> requestEntity =
        new HttpEntity<>(request, buildRequestHeaders(accessToken));
    log.debug("The url used to create a user in oneCentral is : {}", url);
    return makeRequest(url, HttpMethod.POST, requestEntity, OneCentralUser.class);
  }
}
