package com.ssp.geneva.sdk.onecentral.repository;

import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.RESET_PASSWORD_URL_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfigProperties;
import com.ssp.geneva.sdk.onecentral.model.PasswordUpdateRequest;
import com.ssp.geneva.sdk.onecentral.util.BuildUri;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/** Resource Path: /user-management/v6/users/{username}/password */
@Log4j2
public class UserManagementPasswordRepository extends BaseRestRepository {

  public UserManagementPasswordRepository(
      final ObjectMapper oneCentralObjectMapper,
      final RestTemplate s2sTemplate,
      final OneCentralSdkConfigProperties oneCentralSdkConfigProperties) {
    super(oneCentralObjectMapper, s2sTemplate, oneCentralSdkConfigProperties.getSsoOneApiBaseUrl());
  }

  /**
   * PUT: Update user password.
   *
   * @param request {@link PasswordUpdateRequest}
   * @return {@link ResponseEntity} with service request response.
   */
  public ResponseEntity<String> reset(String accessToken, PasswordUpdateRequest request) {
    var resourcePath = RESET_PASSWORD_URL_PATH.getResourcePath();
    Map<String, String> pathVars = Map.of("username", request.getUsername());
    String url = BuildUri.build(this.baseUrl, resourcePath, pathVars);
    log.debug("The url used to reset password in oneCentral is : {}", url);
    HttpEntity<String> requestEntity =
        buildRequestBody(accessToken, request.getOldPassword(), request.getNewPassword());
    ResponseEntity<String> response = makeRequest(url, HttpMethod.PUT, requestEntity, String.class);
    return response;
  }

  private HttpEntity<String> buildRequestBody(
      String accessToken, String oldPassword, String newPassword) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode node = mapper.createObjectNode();
    node.put("oldpassword", oldPassword);
    node.put("newpassword", newPassword);
    return new HttpEntity<>(node.toString(), buildRequestHeaders(accessToken));
  }
}
