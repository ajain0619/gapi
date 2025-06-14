package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.common.collect.Sets;
import com.nexage.app.dto.AccessTokenDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.sdk.identityb2b.IdentityB2bSdkClient;
import com.ssp.geneva.sdk.identityb2b.config.IdentityB2bSdkConfigProperties;
import com.ssp.geneva.sdk.identityb2b.exception.IdentityB2bErrorCodes;
import com.ssp.geneva.sdk.identityb2b.exception.IdentityB2bSdkException;
import com.ssp.geneva.sdk.identityb2b.model.B2bAccessToken;
import com.ssp.geneva.sdk.identityb2b.repository.AccessTokenRepository;
import com.ssp.geneva.sdk.identityb2b.repository.UserAuthenticationRepository;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

  private static final String ACCESS_TOKEN_VALUE = "52acc0ab-77ce-4641-8b26-95b2381375ac";
  private static final String TOKEN_EXPIRATION = "500";

  @Mock OAuth2RestTemplate restTemplate;
  @Mock AccessTokenRepository accessTokenRepository;
  @Mock UserAuthenticationRepository userAuthenticationRepository;
  @Mock IdentityB2bSdkConfigProperties identityB2bSdkConfigProperties;
  @Mock HttpServletRequest request;

  @Mock HttpSessionOAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

  IdentityB2bSdkClient identityB2bSdkClient;
  TokenServiceImpl tokenService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    identityB2bSdkClient =
        IdentityB2bSdkClient.builder()
            .identityB2bSdkConfigProperties(identityB2bSdkConfigProperties)
            .accessTokenRepository(accessTokenRepository)
            .userAuthenticationRepository(userAuthenticationRepository)
            .build();
    tokenService =
        new TokenServiceImpl(restTemplate, identityB2bSdkClient, oAuth2AuthorizedClientRepository);
  }

  @Test
  void shouldReturnSuccessfullyGetTokenByRefreshToken() {
    B2bAccessToken b2bAccessToken = new B2bAccessToken();
    b2bAccessToken.setAccess_token(ACCESS_TOKEN_VALUE);
    b2bAccessToken.setExpires_in(TOKEN_EXPIRATION);

    when(restTemplate.getOAuth2ClientContext()).thenReturn(getOauthContext());
    when(accessTokenRepository.getAccessTokenByRefreshToken(anyString()))
        .thenReturn(new ResponseEntity<>(b2bAccessToken, HttpStatus.OK));

    Set<String> qf = Sets.newHashSet("status");

    String qt = "current";
    AccessTokenDTO accessTokenDTO = tokenService.getToken(qt, qf);
    assertNotNull(accessTokenDTO);
    assertEquals(ACCESS_TOKEN_VALUE, accessTokenDTO.getAccessToken());
    assertEquals(Long.valueOf(TOKEN_EXPIRATION), accessTokenDTO.getExpiresIn());
  }

  @Test
  void shouldFailNotAuthorizedGetTokenByRefreshToken() {
    when(restTemplate.getOAuth2ClientContext()).thenReturn(null);

    Set<String> qf = Sets.newHashSet("status");

    String qt = "current";

    var exception =
        assertThrows(GenevaSecurityException.class, () -> tokenService.getToken(qt, qf));

    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldFailNotFoundGetTokenByRefreshToken() {
    B2bAccessToken b2bAccessToken = new B2bAccessToken();
    b2bAccessToken.setAccess_token(ACCESS_TOKEN_VALUE);
    b2bAccessToken.setExpires_in(TOKEN_EXPIRATION);

    when(restTemplate.getOAuth2ClientContext()).thenReturn(getOauthContext());
    when(identityB2bSdkClient.getAccessTokenRepository().getAccessTokenByRefreshToken(anyString()))
        .thenThrow(new RestClientException("Test error"));

    Set<String> qf = Sets.newHashSet("status");

    String qt = "current";

    var exception =
        assertThrows(GenevaAppRuntimeException.class, () -> tokenService.getToken(qt, qf));

    assertEquals(ServerErrorCodes.SERVER_UNABLE_TO_GET_TOKEN, exception.getErrorCode());
  }

  @Test
  void shouldFailInternalErrorByAccessToken() {
    B2bAccessToken b2bAccessToken = new B2bAccessToken();
    b2bAccessToken.setAccess_token(ACCESS_TOKEN_VALUE);
    b2bAccessToken.setExpires_in(TOKEN_EXPIRATION);

    when(restTemplate.getOAuth2ClientContext()).thenReturn(getOauthContext());
    when(identityB2bSdkClient.getAccessTokenRepository().getAccessTokenByRefreshToken(anyString()))
        .thenThrow(new RuntimeException("Test error"));

    Set<String> qf = Sets.newHashSet("status");

    String qt = "current";

    var exception = assertThrows(GenevaException.class, () -> tokenService.getToken(qt, qf));

    assertEquals(ServerErrorCodes.SERVER_TOKEN_INTERNAL_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldFailB2BErrorByAccessToken() {
    B2bAccessToken b2bAccessToken = new B2bAccessToken();
    b2bAccessToken.setAccess_token(ACCESS_TOKEN_VALUE);
    b2bAccessToken.setExpires_in(TOKEN_EXPIRATION);

    when(restTemplate.getOAuth2ClientContext()).thenReturn(getOauthContext());
    when(identityB2bSdkClient.getAccessTokenRepository().getAccessTokenByRefreshToken(anyString()))
        .thenThrow(new IdentityB2bSdkException(IdentityB2bErrorCodes.IDENTITY_B2B_NOT_AUTHORIZED));

    Set<String> qf = Sets.newHashSet("status");

    String qt = "current";

    var exception = assertThrows(GenevaException.class, () -> tokenService.getToken(qt, qf));

    assertEquals(ServerErrorCodes.SERVER_B2B_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenGetTokenWithInvalidQueryParams() {
    var exception =
        assertThrows(GenevaValidationException.class, () -> tokenService.getToken("", null));
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  private OAuth2ClientContext getOauthContext() {
    return new OAuth2ClientContext() {
      @Override
      public OAuth2AccessToken getAccessToken() {
        return new OAuth2AccessToken() {
          @Override
          public Map<String, Object> getAdditionalInformation() {
            return null;
          }

          @Override
          public Set<String> getScope() {
            return null;
          }

          @Override
          public OAuth2RefreshToken getRefreshToken() {
            return new OAuth2RefreshToken() {
              @Override
              public String getValue() {
                return "b89fba14-24ac-481b-a1e2-664c81af8888";
              }
            };
          }

          @Override
          public String getTokenType() {
            return null;
          }

          @Override
          public boolean isExpired() {
            return false;
          }

          @Override
          public Date getExpiration() {
            return null;
          }

          @Override
          public int getExpiresIn() {
            return 0;
          }

          @Override
          public String getValue() {
            return null;
          }
        };
      }

      @Override
      public void setAccessToken(OAuth2AccessToken oAuth2AccessToken) {}

      @Override
      public AccessTokenRequest getAccessTokenRequest() {
        return null;
      }

      @Override
      public void setPreservedState(String s, Object o) {}

      @Override
      public Object removePreservedState(String s) {
        return null;
      }
    };
  }
}
