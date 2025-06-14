package com.ssp.geneva.common.security.service;

import com.ssp.geneva.sdk.onecentral.model.OneCentralUserAuthResponse;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserRolesResponse;
import org.springframework.http.ResponseEntity;

public interface UserAuthorizationService {
  ResponseEntity<OneCentralUserAuthResponse> getUserAuthorization(String accessToken);

  ResponseEntity<OneCentralUserRolesResponse> assignRole(
      String accessToken, String roleId, String userId);
}
