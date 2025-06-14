package com.nexage.app.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nexage.app.config.security.SecureHeadersFilter.SecureHttpHeader;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

class SecureHeadersFilterTest {

  private final Map<String, String> headerMap = new HashMap<>();

  @Test
  void checkSecurityHeaders() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);

    doAnswer(
            invocationOnMock -> {
              headerMap.put(
                  invocationOnMock.getArguments()[0].toString(),
                  invocationOnMock.getArguments()[1].toString());
              return null;
            })
        .when(response)
        .addHeader(anyString(), anyString());
    SecureHeadersFilter filter = new SecureHeadersFilter();
    filter.doFilter(request, response, chain);
    verify(response, times(7)).addHeader(anyString(), anyString());
    assertTrue(headerMap.containsKey(SecureHttpHeader.CONTENT_TYPE_OPTIONS.getHeaderName()));
    assertEquals(
        headerMap.get(SecureHttpHeader.CONTENT_TYPE_OPTIONS.getHeaderName()),
        SecureHttpHeader.CONTENT_TYPE_OPTIONS.getHeaderValue());
    assertTrue(headerMap.containsKey(SecureHttpHeader.EXPECT_CT.getHeaderName()));
    assertTrue(headerMap.containsKey(SecureHttpHeader.FRAME_OPTIONS.getHeaderName()));
    assertEquals(
        headerMap.get(SecureHttpHeader.FRAME_OPTIONS.getHeaderName()),
        SecureHttpHeader.FRAME_OPTIONS.getHeaderValue());
    assertTrue(headerMap.containsKey(SecureHttpHeader.REFERER_POLICY.getHeaderName()));
    assertEquals(
        headerMap.get(SecureHttpHeader.REFERER_POLICY.getHeaderName()),
        SecureHttpHeader.REFERER_POLICY.getHeaderValue());
    assertTrue(headerMap.containsKey(SecureHttpHeader.STRICT_TRANSPORT_SECURITY.getHeaderName()));
    assertEquals(
        headerMap.get(SecureHttpHeader.STRICT_TRANSPORT_SECURITY.getHeaderName()),
        SecureHttpHeader.STRICT_TRANSPORT_SECURITY.getHeaderValue());
    assertTrue(headerMap.containsKey(SecureHttpHeader.XSS_PROTECTION.getHeaderName()));
    assertEquals(
        headerMap.get(SecureHttpHeader.XSS_PROTECTION.getHeaderName()),
        SecureHttpHeader.XSS_PROTECTION.getHeaderValue());
    assertTrue(headerMap.containsKey(SecureHttpHeader.CSP.getHeaderName()));
    assertEquals(
        SecureHttpHeader.CSP.getHeaderValue(), headerMap.get(SecureHttpHeader.CSP.getHeaderName()));
  }
}
