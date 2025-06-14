package com.nexage.app.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class CurrentUserControllerAdviceTest {

  @Mock HttpServletRequest mockRequest;

  @Mock Authentication auth;

  @Mock SpringUserDetails userDetails;

  CurrentUserControllerAdvice advice = new CurrentUserControllerAdvice();

  @Test
  void testAuthenticatedUser() {
    when(auth.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getPid()).thenReturn(1234L);
    Long val = advice.currentUserId(mockRequest, auth);
    assertNotNull(val);
    assertEquals(1234L, val.longValue());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "healthCheck",
        "awsHealthCheck",
        "marklog",
        "dealCacheRefreshAll",
        "unauthorized",
        "swagger",
        "/webjars",
        "/v2/api-docs",
        "/geneva/v1/sellers/54573/creatives/123456abcDEF",
        "/geneva/v1/sellers/1/creatives/123456abcDEF"
      })
  void shouldPassWithValidMatchingUri(String uri) {
    when(mockRequest.getRequestURI()).thenReturn(uri);
    Long val = advice.currentUserId(mockRequest, auth);
    assertNotNull(val);
    assertEquals(0L, val.longValue());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/geneva/v1/sellers/54573/creatives/<script>",
        "/geneva/v1/sellers/54573/creatives/yrsnw",
        "/geneva/v1/sellers/54573/creatives/",
        "/geneva/v1/sellers/54573/creatives/!@#$123",
        "/geneva/v1/sellers/msft/creatives/123456abcDEF",
        "/geneva/v1/sellers//creatives/123456abcDEF"
      })
  void shouldThrowExceptionWithInvalidRequestUri(String uri) {
    when(mockRequest.getRequestURI()).thenReturn(uri);
    var exception =
        assertThrows(GenevaSecurityException.class, () -> advice.currentUserId(mockRequest, auth));

    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }
}
