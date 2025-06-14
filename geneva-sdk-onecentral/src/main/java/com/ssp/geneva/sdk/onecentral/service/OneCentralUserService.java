package com.ssp.geneva.sdk.onecentral.service;

import com.ssp.geneva.sdk.onecentral.dto.OneCentralExtendedUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserResponseDTO.OneCentralUser;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserRolesResponse;
import com.ssp.geneva.sdk.onecentral.repository.AuthorizationManagementRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserManagementRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;

@Log4j2
public class OneCentralUserService {

  private static final String SELLER_SEAT_SUFFIX = "_SELLER_SEAT";
  private final UserManagementRepository userManagementRepository;
  private final AuthorizationManagementRepository authorizationManagementRepository;

  public OneCentralUserService(
      UserManagementRepository userManagementRepository,
      AuthorizationManagementRepository authorizationManagementRepository) {
    this.userManagementRepository = userManagementRepository;
    this.authorizationManagementRepository = authorizationManagementRepository;
  }

  /**
   * PUT: Update an user, including it's assigned roles.
   *
   * @param request {@link OneCentralUserRequestDTO}
   * @return {@link ResponseEntity} of type {@link OneCentralUser} with service request response.
   */
  public ResponseEntity<OneCentralUser> updateUser(
      String accessToken, OneCentralExtendedUserRequestDTO request) {
    ResponseEntity<OneCentralUser> response =
        userManagementRepository.updateUser(accessToken, request.getUserRequestDTO());
    log.info("Successfully updated user data: {}", response);
    var userResponse = response.getBody();
    if (userResponse != null) {
      updateUserRoles(accessToken, request);
    }
    return response;
  }

  private void updateUserRoles(String accessToken, OneCentralExtendedUserRequestDTO requestDTO) {
    log.info("Updating user role: {}", requestDTO.getRoleName());
    if (requestDTO.getRoleName() != null) {
      var requestedRoleIds = new long[requestDTO.isSellerSeatEnabled() ? 2 : 1];
      requestedRoleIds[0] =
          authorizationManagementRepository.getOneCentralRole(requestDTO.getRoleName());
      if (requestDTO.isSellerSeatEnabled()) {
        var sellerSeatRoleName = inferSellerSearRole(requestDTO.getRoleName());
        requestedRoleIds[1] =
            authorizationManagementRepository.getOneCentralRole(sellerSeatRoleName);
      }
      log.info("Requested roles: {}", Arrays.toString(requestedRoleIds));

      var rolesResponse =
          authorizationManagementRepository.getUserRoles(
              accessToken, requestDTO.getUserRequestDTO());
      var rolesList =
          removeNonGenevaRoles(
              rolesResponse.getBody() != null ? rolesResponse.getBody().getList() : List.of());
      log.info("Roles from OneCentral: {}", rolesList);
      var rolesToDelete = unmatchedRoles(rolesList, requestedRoleIds);
      log.info("Roles to delete: {}", rolesToDelete);
      var username = requestDTO.getUserRequestDTO().getUsername();
      rolesToDelete.stream()
          .forEach(
              role ->
                  authorizationManagementRepository.deleteUserRole(
                      accessToken, username, role.getId()));
      var rolesToAdd = matchedRoles(rolesList, requestedRoleIds);
      log.info("Roles to add: {}", rolesToAdd);
      rolesToAdd.stream()
          .forEach(
              roleId ->
                  authorizationManagementRepository.assignUserRole(accessToken, username, roleId));
    }
  }

  private String inferSellerSearRole(String roleName) {
    var start = roleName.substring(0, roleName.lastIndexOf("_"));
    return start + SELLER_SEAT_SUFFIX;
  }

  private List<OneCentralUserRolesResponse> removeNonGenevaRoles(
      List<OneCentralUserRolesResponse> roles) {
    return roles.stream()
        .filter(
            role -> authorizationManagementRepository.isGenevaRole(Long.parseLong(role.getId())))
        .collect(Collectors.toList());
  }

  private List<OneCentralUserRolesResponse> unmatchedRoles(
      List<OneCentralUserRolesResponse> roles, long[] roleIds) {
    return roles.stream()
        .filter(role -> Arrays.stream(roleIds).noneMatch(id -> id == Long.parseLong(role.getId())))
        .collect(Collectors.toList());
  }

  private List<Long> matchedRoles(List<OneCentralUserRolesResponse> roles, long[] roleIds) {
    return Arrays.stream(roleIds)
        .boxed()
        .filter(
            roleId -> roles.stream().noneMatch(role -> roleId.equals(Long.parseLong(role.getId()))))
        .collect(Collectors.toList());
  }
}
