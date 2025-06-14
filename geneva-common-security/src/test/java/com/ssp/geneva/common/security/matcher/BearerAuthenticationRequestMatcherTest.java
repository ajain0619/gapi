package com.ssp.geneva.common.security.matcher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

class BearerAuthenticationRequestMatcherTest {

  HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

  @Test
  void whenRequestContainsBearerTokenExpectTrue() {
    BearerAuthenticationRequestMatcher bearerAuthenticationRequestMatcher =
        new BearerAuthenticationRequestMatcher();

    when(request.getHeader(HttpHeaders.AUTHORIZATION))
        .thenReturn("Bearer 48be92f1-744e-48dc-b5b4-1639cd5174c5");

    assertTrue(bearerAuthenticationRequestMatcher.matches(request));
  }

  @Test
  void whenRequestContainsBearerTokenStartingWhichStartsWithLowercaseLetterExpectTrue() {
    BearerAuthenticationRequestMatcher bearerAuthenticationRequestMatcher =
        new BearerAuthenticationRequestMatcher();

    when(request.getHeader(HttpHeaders.AUTHORIZATION))
        .thenReturn("bearer 48be92f1-744e-48dc-b5b4-1639cd5174c5");

    assertTrue(bearerAuthenticationRequestMatcher.matches(request));
  }

  @Test
  void whenRequestDoesNotContainAuthorizationHeaderExpectFalse() {
    BearerAuthenticationRequestMatcher bearerAuthenticationRequestMatcher =
        new BearerAuthenticationRequestMatcher();

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    assertFalse(bearerAuthenticationRequestMatcher.matches(request));
  }

  @Test
  void whenRequestContainsNonBearerAuthorizationHeaderExpectFalse() {
    BearerAuthenticationRequestMatcher bearerAuthenticationRequestMatcher =
        new BearerAuthenticationRequestMatcher();

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic Ym9zY236Ym9zY27=");

    assertFalse(bearerAuthenticationRequestMatcher.matches(request));
  }
}
