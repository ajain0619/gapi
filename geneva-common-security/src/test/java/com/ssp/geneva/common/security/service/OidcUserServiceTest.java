package com.ssp.geneva.common.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.model.UserAuth;
import com.ssp.geneva.common.security.oauth2.s2s.Oauth2AuthorizedClientProvider;
import com.ssp.geneva.common.security.util.UserTestData;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkErrorResponse;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserAuthResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class OidcUserServiceTest {
  @Mock OneCentralUserDetailsService userDetailsService;
  @Mock UserAuthorizationService userAuthorizationService;
  @Mock OidcUserRequest oidcUserRequest;

  @Mock ClientRegistration clientRegistration;
  @Mock ClientRegistration.ProviderDetails providerDetails;
  @Mock ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint;

  @Mock ResponseEntity responseEntity;

  @Mock OAuth2AccessToken auth2AccessToken;

  @Mock OidcIdToken oidcIdToken;

  @Mock Oauth2AuthorizedClientProvider clientProvider;

  @Mock OAuth2AuthorizedClient oAuth2AuthorizedClient;

  @Mock OAuth2AccessToken oAuth2AccessToken;

  @InjectMocks OidcUserService userService;

  @BeforeEach
  void SetUp() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  void shouldLoadUser() {
    when(oidcUserRequest.getAccessToken()).thenReturn(auth2AccessToken);
    when(oidcUserRequest.getIdToken()).thenReturn(oidcIdToken);
    when(auth2AccessToken.getTokenValue()).thenReturn("af345ab");
    when(userAuthorizationService.getUserAuthorization(anyString())).thenReturn(responseEntity);
    OneCentralUserAuthResponse oneCentralUserAuthResponse = new OneCentralUserAuthResponse();
    oneCentralUserAuthResponse.setCdid("test1234");
    oneCentralUserAuthResponse.setStatus("ACTIVE");
    when(responseEntity.getBody()).thenReturn(oneCentralUserAuthResponse);
    when(oidcUserRequest.getClientRegistration()).thenReturn(clientRegistration);
    when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
    when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
    when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("userName");
    when(clientProvider.getAuthorizedClient()).thenReturn(oAuth2AuthorizedClient);
    when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getTokenValue()).thenReturn("testToken");
    var user = UserTestData.createUser();

    UserAuth userAuth = new UserAuth(user, Collections.emptyList());
    when(userDetailsService.loadUserBy1CUsername(any(Map.class), eq(false))).thenReturn(userAuth);
    SpringUserDetails springUserDetails = new SpringUserDetails(userAuth);
    when(userDetailsService.loadUserDetailsBy1CUsername(any(Map.class), eq(false)))
        .thenReturn(springUserDetails);
    var oidcUser = userService.loadUser(oidcUserRequest);
    assertEquals(oidcIdToken, oidcUser.getIdToken());
    assertEquals(user.getUserName(), oidcUser.getAttribute("userName"));
  }

  @Test
  void shouldLoadUserWithOneCentralMigrationStatus() {
    when(oidcUserRequest.getAccessToken()).thenReturn(auth2AccessToken);
    when(oidcUserRequest.getIdToken()).thenReturn(oidcIdToken);
    when(auth2AccessToken.getTokenValue()).thenReturn("af345ab");
    when(userAuthorizationService.getUserAuthorization(anyString())).thenReturn(responseEntity);
    OneCentralUserAuthResponse oneCentralUserAuthResponse = new OneCentralUserAuthResponse();
    oneCentralUserAuthResponse.setCdid("test1234");
    oneCentralUserAuthResponse.setStatus("ACTIVE");
    when(responseEntity.getBody()).thenReturn(oneCentralUserAuthResponse);
    when(oidcUserRequest.getClientRegistration()).thenReturn(clientRegistration);
    when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
    when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
    when(clientProvider.getAuthorizedClient()).thenReturn(oAuth2AuthorizedClient);
    when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getTokenValue()).thenReturn("testToken");
    when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("userName");
    var user = UserTestData.createUser();
    user.setMigratedOneCentral(false);

    UserAuth userAuth = new UserAuth(user, Collections.emptyList());
    SpringUserDetails springUserDetails = new SpringUserDetails(userAuth);
    when(userDetailsService.loadUserBy1CUsername(any(Map.class), eq(false))).thenReturn(userAuth);
    when(userDetailsService.loadUserDetailsBy1CUsername(any(Map.class), eq(false)))
        .thenReturn(springUserDetails);
    var oidcUser = userService.loadUser(oidcUserRequest);
    assertEquals(oidcIdToken, oidcUser.getIdToken());
    assertEquals(user.getUserName(), oidcUser.getAttribute("userName"));
  }

  @Test
  void shouldThroughAuthenticationServiceExceptionWhenLoadUserHasInvalidS2SToken() {
    when(oidcUserRequest.getAccessToken()).thenReturn(auth2AccessToken);
    when(auth2AccessToken.getTokenValue()).thenReturn("af345ab");
    when(userAuthorizationService.getUserAuthorization(anyString())).thenReturn(responseEntity);
    OneCentralUserAuthResponse oneCentralUserAuthResponse = new OneCentralUserAuthResponse();
    oneCentralUserAuthResponse.setCdid("test1234");
    oneCentralUserAuthResponse.setStatus("ACTIVE");
    when(responseEntity.getBody()).thenReturn(oneCentralUserAuthResponse);
    when(clientProvider.getAuthorizedClient()).thenReturn(oAuth2AuthorizedClient);
    when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getTokenValue())
        .thenThrow(new OneCentralException(OneCentralErrorCodes.ONECENTRAL_UNABLE_TO_GET_TOKEN));

    var user = UserTestData.createUser();
    user.setMigratedOneCentral(false);

    UserAuth userAuth = new UserAuth(user, Collections.emptyList());
    SpringUserDetails springUserDetails = new SpringUserDetails(userAuth);
    when(userDetailsService.loadUserBy1CUsername(any(Map.class), eq(false))).thenReturn(userAuth);
    assertThrows(AuthenticationServiceException.class, () -> userService.loadUser(oidcUserRequest));
  }

  @Test
  void shouldIgnoreRoleAssignmentAlreadyExistsWhenLoadUserCalled() {
    OneCentralSdkErrorResponse.OneCentralErrorResponseBody responseBody =
        new OneCentralSdkErrorResponse.OneCentralErrorResponseBody();
    responseBody.setMessage("Entity Role Assignment already exists in the system");
    when(oidcUserRequest.getAccessToken()).thenReturn(auth2AccessToken);

    when(auth2AccessToken.getTokenValue()).thenReturn("af345ab");
    when(userAuthorizationService.assignRole(any(), any(), any()))
        .thenThrow(
            new OneCentralException(
                OneCentralErrorCodes.ONECENTRAL_INTERNAL_ERROR,
                OneCentralSdkErrorResponse.builder()
                    .oneCentralErrors(List.of(responseBody))
                    .build()));
    when(oidcUserRequest.getAccessToken()).thenReturn(auth2AccessToken);
    when(oidcUserRequest.getIdToken()).thenReturn(oidcIdToken);
    when(auth2AccessToken.getTokenValue()).thenReturn("af345ab");
    when(userAuthorizationService.getUserAuthorization(anyString())).thenReturn(responseEntity);
    OneCentralUserAuthResponse oneCentralUserAuthResponse = new OneCentralUserAuthResponse();
    oneCentralUserAuthResponse.setCdid("test1234");
    oneCentralUserAuthResponse.setStatus("ACTIVE");
    when(responseEntity.getBody()).thenReturn(oneCentralUserAuthResponse);
    when(oidcUserRequest.getClientRegistration()).thenReturn(clientRegistration);
    when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
    when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
    when(clientProvider.getAuthorizedClient()).thenReturn(oAuth2AuthorizedClient);
    when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(oAuth2AccessToken);
    when(oAuth2AccessToken.getTokenValue()).thenReturn("testToken");
    when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("userName");
    var user = UserTestData.createUser();
    user.setMigratedOneCentral(false);

    UserAuth userAuth = new UserAuth(user, Collections.emptyList());
    SpringUserDetails springUserDetails = new SpringUserDetails(userAuth);
    when(userDetailsService.loadUserBy1CUsername(any(Map.class), eq(false))).thenReturn(userAuth);
    when(userDetailsService.loadUserDetailsBy1CUsername(any(Map.class), eq(false)))
        .thenReturn(springUserDetails);
    var oidcUser = userService.loadUser(oidcUserRequest);
    assertEquals(oidcIdToken, oidcUser.getIdToken());
    assertEquals(user.getUserName(), oidcUser.getAttribute("userName"));
  }

  @Test
  void shouldThroughAuthenticationServiceExceptionWhenLoadUserHasInactiveOneCentralUser() {
    when(oidcUserRequest.getAccessToken()).thenReturn(auth2AccessToken);
    when(auth2AccessToken.getTokenValue()).thenReturn("af345ab");
    when(userAuthorizationService.getUserAuthorization(anyString())).thenReturn(responseEntity);
    OneCentralUserAuthResponse oneCentralUserAuthResponse = new OneCentralUserAuthResponse();
    oneCentralUserAuthResponse.setCdid("test1234");
    oneCentralUserAuthResponse.setStatus("INACTIVE");
    when(responseEntity.getBody()).thenReturn(oneCentralUserAuthResponse);
    assertThrows(AuthenticationServiceException.class, () -> userService.loadUser(oidcUserRequest));
  }
}
