package com.ssp.geneva.sdk.xandr.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import com.ssp.geneva.sdk.xandr.error.XandrSdkErrorCodes;
import com.ssp.geneva.sdk.xandr.exception.XandrSdkException;
import com.ssp.geneva.sdk.xandr.model.AuthResponse;
import com.ssp.geneva.sdk.xandr.model.XandrResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AuthRepositoryTest {

  private String authToken = "b89fba14-24ac-481b-a1e2-664c81af8888";

  private String xandrEndpoint = "localhost:8080";

  private String xandrCredentials =
      """
    {
      "username": "USERNAME",
      "password": "PASSWORD"
    }
    """;

  private String xandrCredentialsMsRebroadcast =
      """
    {
      "username": "USERNAME2",
      "password": "PASSWORD2"
    }
    """;

  private String badCredentialString =
      """
    {
      "username: "USERNAME",
      "password": "PASSWORD"
    }
    """;

  @InjectMocks private AuthRepository authRepository;

  @Mock RestTemplate xandrRestTemplate;

  @BeforeEach
  void setUp() {
    authRepository =
        new AuthRepository(
            xandrEndpoint, xandrRestTemplate, xandrCredentials, xandrCredentialsMsRebroadcast);
  }

  @Test
  void shouldReturnAuthToken() {
    AuthResponse authResponse =
        AuthResponse.builder()
            .response(XandrResponse.<String>builder().content(authToken).build())
            .build();
    ResponseEntity<AuthResponse> tokenResponse = ResponseEntity.of(Optional.of(authResponse));
    when(xandrRestTemplate.exchange(any(RequestEntity.class), eq(AuthResponse.class)))
        .thenReturn(tokenResponse);

    String token = authRepository.getAuthToken(xandrCredentials);

    assertNotNull(token);
    assertEquals(authToken, token);
  }

  @Test
  void shouldReturnAuthHeaderWithToken() {
    AuthResponse authResponse =
        AuthResponse.builder()
            .response(XandrResponse.<String>builder().content(authToken).build())
            .build();
    ResponseEntity<AuthResponse> tokenResponse = ResponseEntity.of(Optional.of(authResponse));
    when(xandrRestTemplate.exchange(any(RequestEntity.class), eq(AuthResponse.class)))
        .thenReturn(tokenResponse);

    HttpHeaders httpHeaders = authRepository.getAuthHeader(xandrCredentials);
    assertNotNull(httpHeaders);
    assertNotNull(httpHeaders.get("Authorization"));
    assertEquals(authToken, httpHeaders.get("Authorization").get(0));
  }

  @Test
  void shouldReturnAuthHeaderForXandr() {
    AuthResponse authResponse =
        AuthResponse.builder()
            .response(XandrResponse.<String>builder().content(authToken).build())
            .build();
    ResponseEntity<AuthResponse> tokenResponse = ResponseEntity.of(Optional.of(authResponse));
    when(xandrRestTemplate.exchange(any(RequestEntity.class), eq(AuthResponse.class)))
        .thenReturn(tokenResponse);

    HttpHeaders httpHeaders = authRepository.getAuthHeaderForXandr();
    assertNotNull(httpHeaders);
    assertNotNull(httpHeaders.get("Authorization"));
    assertEquals(authToken, httpHeaders.get("Authorization").get(0));
  }

  @Test
  void shouldReturnAuthHeaderForXandrMsRebroadcast() {
    AuthResponse authResponse =
        AuthResponse.builder()
            .response(XandrResponse.<String>builder().content(authToken).build())
            .build();
    ResponseEntity<AuthResponse> tokenResponse = ResponseEntity.of(Optional.of(authResponse));
    when(xandrRestTemplate.exchange(any(RequestEntity.class), eq(AuthResponse.class)))
        .thenReturn(tokenResponse);

    HttpHeaders httpHeaders = authRepository.getAuthHeaderForXandrMsRebroadcast();
    assertNotNull(httpHeaders);
    assertNotNull(httpHeaders.get("Authorization"));
    assertEquals(authToken, httpHeaders.get("Authorization").get(0));
  }

  @Test
  void shouldThrowExceptionWithHttpClientError() {
    when(xandrRestTemplate.exchange(any(RequestEntity.class), eq(String.class)))
        .thenThrow(RuntimeException.class);

    var exception =
        assertThrows(XandrSdkException.class, () -> authRepository.getAuthToken(xandrCredentials));
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(XandrSdkErrorCodes.XANDR_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldThrowJsonParseExceptionWhenInvalidInput() {
    var exception =
        assertThrows(
            XandrSdkException.class, () -> authRepository.getAuthToken(badCredentialString));
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(XandrSdkErrorCodes.XANDR_SDK_CREDENTIALS_ERROR, exception.getErrorCode());
  }
}
