package com.nexage.app.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.security.service.UserAuthorizationServiceImpl;
import com.ssp.geneva.sdk.onecentral.OneCentralSdkClient;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserAuthResponse;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserRolesResponse;
import com.ssp.geneva.sdk.onecentral.repository.AuthorizationManagementRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserAuthorizationRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationServiceImplTest {

  @Mock UserAuthorizationRepository userAuthorizationRepository;
  @Mock AuthorizationManagementRepository authorizationManagementRepository;
  @Mock OneCentralSdkClient oneCentralSdkClient;
  @InjectMocks UserAuthorizationServiceImpl userAuthorizationServiceImpl;

  @Test
  void shouldGetUserAuthorization() {

    when(oneCentralSdkClient.getUserAuthorizationRepository())
        .thenReturn(userAuthorizationRepository);
    ResponseEntity<OneCentralUserAuthResponse> response =
        new ResponseEntity("Here goes the user object", HttpStatus.OK);
    when(userAuthorizationRepository.getUserAuth(anyString())).thenReturn(response);
    ResponseEntity<OneCentralUserAuthResponse> result =
        userAuthorizationServiceImpl.getUserAuthorization("12345");
    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

  @Test
  void shouldAssignRole() {
    var accessToken = UUID.randomUUID().toString();
    when(oneCentralSdkClient.getAuthorizationManagementRepository())
        .thenReturn(authorizationManagementRepository);
    ResponseEntity<OneCentralUserRolesResponse> response =
        new ResponseEntity(
            "{\n"
                + "  \"roleId\": 510380,\n"
                + "  \"id\": \"dgarciasantamar\",\n"
                + "  \"type\": \"user\",\n"
                + "  \"organizationIds\": []\n"
                + "}\n",
            HttpStatus.OK);
    when(authorizationManagementRepository.assignRole(eq(accessToken), anyString(), anyString()))
        .thenReturn(response);
    ResponseEntity<OneCentralUserRolesResponse> result =
        userAuthorizationServiceImpl.assignRole(accessToken, "dgarciasantamar", "510380");
    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }
}
