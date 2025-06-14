package com.ssp.geneva.common.security.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

@Log4j2
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

  private static final String USER_LOGOUT_URL_PATH = "identity/XUI/#logout/&&goto=";

  private final String baseUrl;
  private final String baseApplicationUri;

  private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  public LogoutSuccessHandler(String baseUrl, String baseApplicationUri) {
    this.baseUrl = baseUrl;
    this.baseApplicationUri = baseApplicationUri;
    log.debug("baseUrl={}, baseApplicationUri={}", baseUrl, baseApplicationUri);
  }

  @Override
  public void onLogoutSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    log.debug("logout success handler");
    if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
      // For non-sso users, the client executes an ajax request
      // and does not expect to handle a redirect. However when the client
      // is handling a sso user, it expects this redirect.
      String targetUrl = makeUrl(baseUrl, USER_LOGOUT_URL_PATH, baseApplicationUri);

      // redirect to b2b logout
      redirectStrategy.sendRedirect(request, response, targetUrl);
    }
  }

  private String makeUrl(final String baseUrl, final String path, final String redirect) {
    StringBuilder sb = new StringBuilder(baseUrl);
    if (!baseUrl.endsWith("/")) {
      sb.append("/");
    }
    sb.append(path);
    sb.append(redirect);
    return sb.toString();
  }
}
