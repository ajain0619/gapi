package com.ssp.geneva.sdk.onecentral.repository;

import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.ROLE_ASSIGN_URL_PATH;
import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.ROLE_FETCH_URL_PATH;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfigProperties;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.model.OneCentralRolesListResponse;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserRolesResponse;
import com.ssp.geneva.sdk.onecentral.model.Role;
import com.ssp.geneva.sdk.onecentral.util.BuildUri;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/** Defines operations related to user roles. */
@Log4j2
public class AuthorizationManagementRepository extends BaseRestRepository {

  private static final String USER_ENTITY_TYPE = "user";
  private final Set<Role> roles;

  public AuthorizationManagementRepository(
      final ObjectMapper oneCentralObjectMapper,
      final RestTemplate s2sTemplate,
      final OneCentralSdkConfigProperties oneCentralSdkConfigProperties) {
    super(oneCentralObjectMapper, s2sTemplate, oneCentralSdkConfigProperties.getSsoOneApiBaseUrl());
    roles = oneCentralSdkConfigProperties.getRoles();
  }

  /**
   * Fetch all roles assigned to a user.
   *
   * @param request use request DTO
   * @return array of user roles
   */
  public ResponseEntity<OneCentralRolesListResponse> getUserRoles(
      String accessToken, OneCentralUserRequestDTO request) {
    var username = request.getUsername();
    log.debug("Username: {}", username);
    var resourcePath = ROLE_FETCH_URL_PATH.getResourcePath();
    Map<String, String> pathVars = Map.of("type", USER_ENTITY_TYPE, "id", username);
    String url = BuildUri.build(this.baseUrl, resourcePath, pathVars);
    var requestEntity = new HttpEntity<>(buildRequestHeaders(accessToken));
    return makeRequest(url, HttpMethod.GET, requestEntity, OneCentralRolesListResponse.class);
  }

  /**
   * Delete a single role related to user.
   *
   * @param username user ID
   * @param roleId role ID
   * @return deleted role data
   */
  public ResponseEntity<OneCentralUserRolesResponse> deleteUserRole(
      String accessToken, String username, String roleId) {
    var resourcePath = ROLE_ASSIGN_URL_PATH.getResourcePath();
    Map<String, String> pathVars =
        Map.of("type", USER_ENTITY_TYPE, "id", username, "roleId", roleId);
    String url = BuildUri.build(this.baseUrl, resourcePath, pathVars);
    HttpEntity<OneCentralUserRequestDTO> requestEntity =
        new HttpEntity<>(null, buildRequestHeaders(accessToken));
    log.debug("DELETE URl path: {}", url);
    return makeRequest(url, HttpMethod.DELETE, requestEntity, OneCentralUserRolesResponse.class);
  }

  /**
   * Assign a role to a user.
   *
   * @param accessToken Server 2 Server access token due to operation restriction. OIDC user is not
   *     a valid one.
   * @param userId user ID
   * @param roleId role ID
   * @return created role
   */
  public ResponseEntity<OneCentralUserRolesResponse> assignUserRole(
      String accessToken, String userId, Long roleId) {
    var resourcePath = ROLE_ASSIGN_URL_PATH.getResourcePath();
    HttpEntity<OneCentralUserRequestDTO> requestEntity =
        new HttpEntity<>(null, buildRequestHeaders(accessToken));
    Map<String, String> pathVars =
        Map.of("type", USER_ENTITY_TYPE, "id", userId, "roleId", String.valueOf(roleId));
    String url = BuildUri.build(this.baseUrl, resourcePath, pathVars);
    return makeRequest(url, HttpMethod.POST, requestEntity, OneCentralUserRolesResponse.class);
  }

  public ResponseEntity<OneCentralUserRolesResponse> assignRole(
      String accessToken, String userId, String roleId) {
    return assignUserRole(accessToken, userId, getOneCentralRole(roleId));
  }

  public Long getOneCentralRole(String roleId) {
    try {
      switch (roleId) {
        case "ROLE_ADMIN_NEXAGE":
          return getIdByName("AdminNexage");
        case "ROLE_MANAGER_YIELD_NEXAGE":
          return getIdByName("ManagerYieldNexage");
        case "ROLE_MANAGER_SMARTEX_NEXAGE":
          return getIdByName("ManagerSmartexNexage");
        case "ROLE_MANAGER_NEXAGE":
          return getIdByName("ManagerNexage");
        case "ROLE_USER_NEXAGE":
          return getIdByName("UserNexage");
        case "ROLE_ADMIN_SELLER":
          return getIdByName("AdminSeller");
        case "ROLE_MANAGER_SELLER":
          return getIdByName("ManagerSeller");
        case "ROLE_USER_SELLER":
          return getIdByName("UserSeller");
        case "ROLE_ADMIN_BUYER":
          return getIdByName("AdminBuyer");
        case "ROLE_MANAGER_BUYER":
          return getIdByName("ManagerBuyer");
        case "ROLE_USER_BUYER":
          return getIdByName("UserBuyer");
        case "ROLE_ADMIN_SEATHOLDER":
          return getIdByName("AdminSeatHolder");
        case "ROLE_MANAGER_SEATHOLDER":
          return getIdByName("ManagerSeatHolder");
        case "ROLE_USER_SEAT_HOLDER":
          return getIdByName("UserSeatHolder");
        case "ROLE_ADMIN_SELLER_SEAT":
          return getIdByName("AdminSellerSeat");
        case "ROLE_MANAGER_SELLER_SEAT":
          return getIdByName("ManagerSellerSeat");
        case "ROLE_USER_SELLER_SEAT":
          return getIdByName("UserSellerSeat");
        case "ROLE_API_BUYER":
          return getIdByName("ApiBuyer");
        case "ROLE_API_SELLER":
          return getIdByName("ApiSeller");
        case "ROLE_API_IIQ_NEXAGE":
          return getIdByName("ApiIIQ");
        case "ROLE_DUMMY_NEXAGE":
        case "ROLE_DUMMY_SELLER":
        case "ROLE_DUMMY_BUYER":
          return getIdByName("DummyRole");
        default:
          return null;
      }
    } catch (InvalidParameterException e) {
      log.error("Unable to get onecentral role. Exception={}", e.getMessage());
      return null;
    }
  }

  public boolean isGenevaRole(Long roleId) {
    return roles.stream().anyMatch(id -> id != null && id.getId().equals(roleId));
  }

  private Long getIdByName(String name) {
    Optional<Role> role = roles.stream().filter(r -> r.getName().equals(name)).findFirst();
    if (role.isPresent()) return role.get().getId();
    throw new InvalidParameterException("An existing role cannot be found for the given name");
  }
}
