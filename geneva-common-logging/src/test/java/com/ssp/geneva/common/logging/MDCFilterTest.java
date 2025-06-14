package com.ssp.geneva.common.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.security.Principal;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class MDCFilterTest {

  private static MockedStatic<MDC> mockedSettings;
  private static HttpServletRequest httpServletRequest;
  private static HttpServletResponse httpServletResponse;
  private static Principal principal;
  private static FilterChain filterChain;
  private static MDCFilter mdcFilter;

  @BeforeEach
  void init() {
    mockedSettings = mockStatic(MDC.class);
    httpServletRequest = mock(HttpServletRequest.class);
    httpServletResponse = mock(HttpServletResponse.class);
    principal = mock(Principal.class);
    filterChain = mock(FilterChain.class);
    mdcFilter = mock(MDCFilter.class);
  }

  @AfterEach
  void close() {
    mockedSettings.close();
  }

  @Test
  void doFilterIntervalUsernameAndRequestIdTest() throws Exception {

    given(MDC.get("requestId")).willReturn("l33t-c0d3r");
    given(MDC.get("username")).willReturn("userTest");
    when(httpServletRequest.getHeader("Request-Id")).thenReturn("133t-c0d3r");
    when(httpServletRequest.getUserPrincipal()).thenReturn(principal);
    when(principal.getName()).thenReturn("userTest");
    mdcFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
    assertEquals("l33t-c0d3r", MDC.get("requestId"));
    assertEquals("userTest", MDC.get("username"));
  }

  @Test
  void doFilterIntervalRequestIdTest() throws Exception {
    given(MDC.get("requestId")).willReturn("l33t-c0d3r");
    when(httpServletRequest.getHeader("Request-Id")).thenReturn("133t-c0d3r");
    mdcFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
    assertEquals("l33t-c0d3r", MDC.get("requestId"));
  }

  @Test
  void doFilterIntervalNoInfoTest() throws Exception {
    given(MDC.get("requestId")).willReturn(null);
    given(MDC.get("username")).willReturn(null);
    when(httpServletRequest.getHeader("Request-Id")).thenReturn(null);
    when(httpServletRequest.getUserPrincipal()).thenReturn(principal);
    when(principal.getName()).thenReturn(null);
    mdcFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
    assertNull(MDC.get("requestId"));
  }
}
