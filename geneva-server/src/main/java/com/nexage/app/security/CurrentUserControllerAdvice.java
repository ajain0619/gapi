package com.nexage.app.security;

import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * User details injections.
 *
 * @author Nick Ilkevich
 * @since 16.09.2014
 */
@ControllerAdvice
public class CurrentUserControllerAdvice {

  @ModelAttribute(value = "currentUserPid")
  public Long currentUserId(HttpServletRequest request, Authentication auth) {
    if (auth != null && auth.getPrincipal() instanceof SpringUserDetails springUserDetails) {
      return springUserDetails.getPid();
    } else if (auth != null
        && auth.getPrincipal()
            instanceof OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal) {
      var userDetails = oAuth2AuthenticatedPrincipal.getAttribute("springUserDetails");
      if (userDetails != null) {
        return ((SpringUserDetails) userDetails).getPid();
      }
    }
    String uri = request.getRequestURI();
    if (uri.contains("healthCheck")
        || uri.contains("awsHealthCheck")
        || uri.contains("marklog")
        || uri.contains("dealCacheRefreshAll")
        || uri.contains("unauthorized")
        || uri.contains("swagger")
        || uri.contains("/webjars")
        || uri.contains("/v2/api-docs")
        || Pattern.matches(".*/sellers/[0-9]+/creatives/[A-Fa-f0-9]+", uri)) {
      /*
      TODO:  ControllerAdvice  seems to override anything in the xml, which is  a problem for healthCheck.  Either find a way to remove this class entirely,
      (which itself is a hack which was put here as part of a change to allow non-nexage users to reset their password) or when we upgrade to Spring 4,
      , remove this healthCheck-specific code. (Spring 4 allows you to restrict scope of Controller Advice).  For now, by not throwing an exception here, we allow health check when not logged in .
       */
      return 0L;
    } else {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }
}
