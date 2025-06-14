package com.ssp.geneva.common.security.service;

import com.ssp.geneva.sdk.onecentral.OneCentralSdkClient;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserAuthResponse;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserRolesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class UserAuthorizationServiceImpl implements UserAuthorizationService {

  private final OneCentralSdkClient oneCentralSdkClient;

  @Autowired
  public UserAuthorizationServiceImpl(OneCentralSdkClient oneCentralSdkClient) {
    this.oneCentralSdkClient = oneCentralSdkClient;
  }

  @Override
  public ResponseEntity<OneCentralUserAuthResponse> getUserAuthorization(String accessToken) {
    return oneCentralSdkClient.getUserAuthorizationRepository().getUserAuth(accessToken);
  }

  @Override
  public ResponseEntity<OneCentralUserRolesResponse> assignRole(
      String accessToken, String roleId, String userId) {
    return oneCentralSdkClient
        .getAuthorizationManagementRepository()
        .assignRole(accessToken, userId, roleId);
  }
}
