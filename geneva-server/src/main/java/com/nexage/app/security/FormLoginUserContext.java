package com.nexage.app.security;

import static org.springframework.util.CollectionUtils.isEmpty;

import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRestrictedSiteRepository;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.util.login.LoginEntitlementCorrectionHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

/**
 * Helper class used to retrieve authenticated user from the Security Context. It also contains
 * handy methods performing various security validations.
 */
@Log4j2
@ConditionalOnProperty(prefix = "geneva.server", name = "login", havingValue = "form-login")
@Component("loginUserContext")
public class FormLoginUserContext extends LoginUserContext {

  private final LoginEntitlementCorrectionHandler loginEntitlementCorrectionHandler;

  @Autowired
  public FormLoginUserContext(
      PositionRepository positionRepository,
      SiteRepository siteRepository,
      CompanyRuleRepository companyRuleRepository,
      UserRestrictedSiteRepository userRestrictedSiteRepository,
      LoginEntitlementCorrectionHandler loginEntitlementCorrectionHandler) {
    super(positionRepository, siteRepository, companyRuleRepository, userRestrictedSiteRepository);
    this.loginEntitlementCorrectionHandler = loginEntitlementCorrectionHandler;
  }

  @Override
  public SpringUserDetails getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    // Authentication can be null if no authentication information is available
    if (auth != null) {
      Object principal = auth.getPrincipal();
      if (principal instanceof OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal) {
        principal = oAuth2AuthenticatedPrincipal.getAttribute("springUserDetails");
      }
      if (principal instanceof SpringUserDetails springUserDetails) {
        if (isEmpty(springUserDetails.getEntitlements())) {
          springUserDetails =
              loginEntitlementCorrectionHandler.correctEntitlements(springUserDetails);
        }
        return springUserDetails;
      }
    } else {
      log.error("Authorized user is null; This should never happen");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_BAD_PRINCIPAL);
    }
    return null;
  }
}
