package com.ssp.geneva.common.security.handler.sso;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Log4j2
public class SsoAuthenticationFailureHandler implements AuthenticationFailureHandler {
  private final String redirect_uri;

  public SsoAuthenticationFailureHandler(String uri) {
    super();
    this.redirect_uri = uri;
    log.debug("uri={}", uri);
  }

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    response.sendRedirect(redirect_uri);
  }
}
