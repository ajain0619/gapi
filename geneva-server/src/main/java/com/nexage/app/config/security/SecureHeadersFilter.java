package com.nexage.app.config.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.web.filter.OncePerRequestFilter;

/** Sets security-related headers to all responses. */
public class SecureHeadersFilter extends OncePerRequestFilter {

  private static final String CT_REPORT_URL =
      "http://csp.yahoo.com/beacon/csp?src=yahoocom-expect-ct-report-only";

  @Override
  public void destroy() {}

  @Override
  protected void doFilterInternal(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      FilterChain filterChain)
      throws ServletException, IOException {
    httpServletResponse.addHeader(
        SecureHttpHeader.EXPECT_CT.headerName,
        String.format(SecureHttpHeader.EXPECT_CT.headerValue, CT_REPORT_URL));
    httpServletResponse.addHeader(
        SecureHttpHeader.CONTENT_TYPE_OPTIONS.headerName,
        SecureHttpHeader.CONTENT_TYPE_OPTIONS.headerValue);
    httpServletResponse.addHeader(
        SecureHttpHeader.FRAME_OPTIONS.headerName, SecureHttpHeader.FRAME_OPTIONS.headerValue);
    httpServletResponse.addHeader(
        SecureHttpHeader.XSS_PROTECTION.headerName, SecureHttpHeader.XSS_PROTECTION.headerValue);
    httpServletResponse.addHeader(
        SecureHttpHeader.REFERER_POLICY.headerName, SecureHttpHeader.REFERER_POLICY.headerValue);
    httpServletResponse.addHeader(
        SecureHttpHeader.STRICT_TRANSPORT_SECURITY.headerName,
        SecureHttpHeader.STRICT_TRANSPORT_SECURITY.headerValue);
    httpServletResponse.addHeader(
        SecureHttpHeader.CSP.headerName, SecureHttpHeader.CSP.headerValue);
    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

  @Getter
  enum SecureHttpHeader {
    EXPECT_CT("Expect-CT", "max-age=31536000, report-uri=\"%s\""),
    CONTENT_TYPE_OPTIONS("X-Content-Type-Options", "nosniff"),
    FRAME_OPTIONS("X-Frame-Options", "DENY"),
    XSS_PROTECTION("X-XSS-Protection", "1; mode=block"),
    REFERER_POLICY("Referrer-Policy", "no-referrer-when-downgrade"),
    STRICT_TRANSPORT_SECURITY(
        "Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload"),
    CSP("Content-Security-Policy", "default-src 'self'");

    private String headerName;
    private String headerValue;

    SecureHttpHeader(String name, String value) {
      this.headerName = name;
      this.headerValue = value;
    }
  }
}
