package com.nexage.app.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.User;
import com.ssp.geneva.common.security.model.UserAuth;
import com.ssp.geneva.common.security.service.OneCentralUserDetailsService;
import com.ssp.geneva.common.security.service.UserAuthorizationService;
import com.ssp.geneva.common.security.service.UserInfoImplicitTokenServices;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserAuthResponse;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

@ExtendWith(MockitoExtension.class)
class UserInfoImplicitTokenServicesTest {

  @Mock OneCentralUserDetailsService userDetailsService;
  @Mock UserAuthorizationService userAuthorizationService;
  @Mock UserDetails userDetails;
  @Mock OAuth2RestTemplate s2sTemplate;

  private static final String ACCESS_TOKEN = "123";

  @Test
  void shouldLoadAuthenticationTestWithToken() {
    // given
    var userInfoImplicitTokenServices =
        new UserInfoImplicitTokenServices(
            userDetailsService, userAuthorizationService, true, s2sTemplate);
    var response = new ResponseEntity(getOneCentralUserAuthResponse(), HttpStatus.OK);

    given(userAuthorizationService.getUserAuthorization(anyString())).willReturn(response);
    given(userDetailsService.loadUserDetailsBy1CUsername(anyMap(), anyBoolean()))
        .willReturn(userDetails);
    given(userDetailsService.loadUserBy1CUsername(anyMap(), anyBoolean()))
        .willReturn(new UserAuth(new User(), null));

    when(userAuthorizationService.getUserAuthorization(anyString())).thenReturn(response);
    when(userDetailsService.loadUserDetailsBy1CUsername(anyMap(), anyBoolean()))
        .thenReturn(userDetails);
    when(userDetailsService.loadUserBy1CUsername(anyMap(), anyBoolean()))
        .thenReturn(new UserAuth(new User(), null));
    var oAuth2Authentication = userInfoImplicitTokenServices.loadAuthentication("123");
    assertNotNull(oAuth2Authentication);
  }

  @Test
  void shouldLoadAuthenticationTestWithSSO() {
    // given
    var userInfoImplicitTokenServices =
        new UserInfoImplicitTokenServices(
            userDetailsService, userAuthorizationService, false, s2sTemplate);
    var response = new ResponseEntity(getOneCentralUserAuthResponse(), HttpStatus.OK);

    given(userAuthorizationService.getUserAuthorization(anyString())).willReturn(response);
    given(userDetailsService.loadUserDetailsBy1CUsername(anyMap(), anyBoolean()))
        .willReturn(userDetails);
    given(userDetailsService.loadUserBy1CUsername(anyMap(), anyBoolean()))
        .willReturn(new UserAuth(new User(), null));

    when(userAuthorizationService.getUserAuthorization(anyString())).thenReturn(response);
    when(userDetailsService.loadUserDetailsBy1CUsername(anyMap(), anyBoolean()))
        .thenReturn(userDetails);
    when(userDetailsService.loadUserBy1CUsername(anyMap(), anyBoolean()))
        .thenReturn(new UserAuth(new User(), null));
    var oAuth2Authentication = userInfoImplicitTokenServices.loadAuthentication("123");
    assertNotNull(oAuth2Authentication);
  }

  @Test
  void shouldFailOnLoadAuthenticationTestWithToken() {
    // given
    var userInfoImplicitTokenServices =
        new UserInfoImplicitTokenServices(
            userDetailsService, userAuthorizationService, true, s2sTemplate);
    var response = new ResponseEntity(getOneCentralUserAuthInvalidResponse(), HttpStatus.OK);

    given(userAuthorizationService.getUserAuthorization(anyString())).willReturn(response);

    // when & then
    assertThrows(
        InvalidTokenException.class,
        () -> userInfoImplicitTokenServices.loadAuthentication(ACCESS_TOKEN));
  }

  @Test
  void shouldFailOnLoadAuthenticationTestWithSSO() {
    // given
    var userInfoImplicitTokenServices =
        new UserInfoImplicitTokenServices(
            userDetailsService, userAuthorizationService, false, s2sTemplate);
    var response = new ResponseEntity(getOneCentralUserAuthInvalidResponse(), HttpStatus.OK);

    given(userAuthorizationService.getUserAuthorization(anyString())).willReturn(response);

    // when & then
    assertThrows(
        InvalidTokenException.class,
        () -> userInfoImplicitTokenServices.loadAuthentication(ACCESS_TOKEN));
  }

  @Test
  void shouldAssignRoleInOCAndMigrateUser() {
    // given
    var customUserDetails = getCustomUserDetails();
    var userInfoImplicitTokenServices =
        new UserInfoImplicitTokenServices(
            userDetailsService, userAuthorizationService, false, s2sTemplate);
    var response = new ResponseEntity(getOneCentralUserAuthResponse(), HttpStatus.OK);

    given(userAuthorizationService.getUserAuthorization(anyString())).willReturn(response);
    given(userDetailsService.loadUserDetailsBy1CUsername(anyMap(), anyBoolean()))
        .willReturn(customUserDetails);
    given(userDetailsService.loadUserBy1CUsername(anyMap(), anyBoolean()))
        .willReturn(new UserAuth(new User(), null))
        .willReturn(new UserAuth(new User(), null));

    User user = new User();
    when(userDetailsService.loadUserBy1CUsername(anyMap(), anyBoolean()))
        .thenReturn(new UserAuth(user, null));
    when(userDetailsService.loadUserBy1CUsername(anyMap(), anyBoolean()))
        .thenReturn(new UserAuth(new User(), null));
    var oAuth2Authentication = userInfoImplicitTokenServices.loadAuthentication("123");
    assertNotNull(oAuth2Authentication);
  }

  @Test
  void shouldFailAndContinueOnAssignRoleInOC() {
    // given
    var role = "ROLE_API";
    var customUserDetails = getCustomUserDetails();
    var userInfoImplicitTokenServices =
        new UserInfoImplicitTokenServices(
            userDetailsService, userAuthorizationService, false, s2sTemplate);
    var response = new ResponseEntity(getOneCentralUserAuthResponse(), HttpStatus.OK);
    var s2sAccessToken = mock(OAuth2AccessToken.class);

    when(userAuthorizationService.getUserAuthorization(anyString())).thenReturn(response);
    when(userDetailsService.loadUserDetailsBy1CUsername(anyMap(), anyBoolean()))
        .thenReturn(customUserDetails);

    var user = new User();
    when(userDetailsService.loadUserBy1CUsername(anyMap(), anyBoolean()))
        .thenReturn(new UserAuth(user, null));
    when(userDetailsService.loadUserBy1CUsername(anyMap(), anyBoolean()))
        .thenReturn(new UserAuth(new User(), null));
    when(userAuthorizationService.assignRole(eq(ACCESS_TOKEN), anyString(), anyString()))
        .thenThrow(new OneCentralException(OneCentralErrorCodes.ONECENTRAL_INTERNAL_ERROR));
    given(s2sTemplate.getAccessToken()).willReturn(s2sAccessToken);
    given(s2sAccessToken.getValue()).willReturn(UUID.randomUUID().toString());

    // when
    var oAuth2Authentication = userInfoImplicitTokenServices.loadAuthentication(ACCESS_TOKEN);

    // then
    assertNotNull(oAuth2Authentication);
  }

  @Test
  void shouldFailAndContinueOnMigrateUser() {
    // given
    var customUserDetails = getCustomUserDetails();
    var userInfoImplicitTokenServices =
        new UserInfoImplicitTokenServices(
            userDetailsService, userAuthorizationService, false, s2sTemplate);
    var response = new ResponseEntity(getOneCentralUserAuthResponse(), HttpStatus.OK);
    var s2sAccessToken = mock(OAuth2AccessToken.class);

    given(userAuthorizationService.getUserAuthorization(anyString())).willReturn(response);
    given(userDetailsService.loadUserDetailsBy1CUsername(anyMap(), anyBoolean()))
        .willReturn(customUserDetails);
    given(userDetailsService.loadUserBy1CUsername(anyMap(), anyBoolean()))
        .willReturn(new UserAuth(new User(), null))
        .willReturn(new UserAuth(new User(), null));
    given(s2sTemplate.getAccessToken()).willReturn(s2sAccessToken);
    given(s2sAccessToken.getValue()).willReturn(UUID.randomUUID().toString());

    doThrow(new RuntimeException("DB error saving user"))
        .when(userDetailsService)
        .updateUserMigrated(null);

    // when
    var oAuth2Authentication = userInfoImplicitTokenServices.loadAuthentication(ACCESS_TOKEN);

    // then
    assertNotNull(oAuth2Authentication);
  }

  @Test
  void shouldFailToReadAccessToken() {
    var userInfoImplicitTokenServices =
        new UserInfoImplicitTokenServices(
            userDetailsService, userAuthorizationService, false, s2sTemplate);
    assertThrows(
        UnsupportedOperationException.class,
        () -> userInfoImplicitTokenServices.readAccessToken("token"));
  }

  private UserDetails getCustomUserDetails() {
    return new UserDetails() {
      private static final long serialVersionUID = 4323024862170401881L;

      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) () -> "ROLE_API");
      }

      @Override
      public String getPassword() {
        return null;
      }

      @Override
      public String getUsername() {
        return null;
      }

      @Override
      public boolean isAccountNonExpired() {
        return false;
      }

      @Override
      public boolean isAccountNonLocked() {
        return false;
      }

      @Override
      public boolean isCredentialsNonExpired() {
        return false;
      }

      @Override
      public boolean isEnabled() {
        return false;
      }
    };
  }

  private OneCentralUserAuthResponse getOneCentralUserAuthResponse() {
    var oneCentralUserAuthResponse = new OneCentralUserAuthResponse();
    oneCentralUserAuthResponse.setUsername("username");
    oneCentralUserAuthResponse.setStatus("ACTIVE");
    return oneCentralUserAuthResponse;
  }

  private OneCentralUserAuthResponse getOneCentralUserAuthInvalidResponse() {
    var oneCentralUserAuthResponse = new OneCentralUserAuthResponse();
    oneCentralUserAuthResponse.setUsername("username");
    oneCentralUserAuthResponse.setStatus("");
    return oneCentralUserAuthResponse;
  }
}
