package com.ssp.geneva.common.security.matcher;

import static org.springframework.util.StringUtils.startsWithIgnoreCase;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.util.matcher.RequestMatcher;

/** Matches requests containing bearer token */
public class BearerAuthenticationRequestMatcher implements RequestMatcher {

  @Override
  public boolean matches(HttpServletRequest request) {
    String value = request.getHeader(HttpHeaders.AUTHORIZATION);
    return startsWithIgnoreCase(value, OAuth2AccessToken.TokenType.BEARER.getValue());
  }
}
