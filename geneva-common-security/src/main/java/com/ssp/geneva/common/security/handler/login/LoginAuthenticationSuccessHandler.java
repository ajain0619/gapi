package com.ssp.geneva.common.security.handler.login;

import com.ssp.geneva.common.security.util.login.LoginEntitlementCorrectionHandler;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
public class LoginAuthenticationSuccessHandler
    extends SavedRequestAwareAuthenticationSuccessHandler {

  protected final LoginEntitlementCorrectionHandler entitlementCorrectionHandler;

  public LoginAuthenticationSuccessHandler(
      LoginEntitlementCorrectionHandler entitlementCorrectionHandler) {
    this.entitlementCorrectionHandler = entitlementCorrectionHandler;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws ServletException, IOException {

    if (log.isDebugEnabled()) {
      log.debug("In authentication success handler");
    }
    if (authentication == null || authentication.getName() == null) {
      log.error("The authentication is null in authentication success handler.");
      throw new RuntimeException("The Authentication object is null!");
    }
    var userAuth = entitlementCorrectionHandler.correctEntitlements(authentication.getName());
    response
        .getWriter()
        .write(entitlementCorrectionHandler.getObjectMapper().writeValueAsString(userAuth));
    response.getWriter().flush();
    response.getWriter().close();
    if (log.isDebugEnabled()) {
      log.debug("Authentication success handler complete");
    }
  }
}
