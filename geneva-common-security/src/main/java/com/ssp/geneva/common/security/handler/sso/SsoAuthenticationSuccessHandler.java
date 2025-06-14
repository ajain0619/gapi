package com.ssp.geneva.common.security.handler.sso;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Log4j2
public class SsoAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  private final String redirect_uri;

  public SsoAuthenticationSuccessHandler(String uri) {
    super();
    this.redirect_uri = uri;
    log.debug("uri={}", uri);
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      Authentication authentication)
      throws IOException {
    httpServletResponse.sendRedirect(redirect_uri);
  }
}
