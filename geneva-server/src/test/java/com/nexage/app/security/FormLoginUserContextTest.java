package com.nexage.app.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRestrictedSiteRepository;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.util.login.LoginEntitlementCorrectionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

@ExtendWith(MockitoExtension.class)
class FormLoginUserContextTest {

  @Mock private PositionRepository positionRepository;
  @Mock private SiteRepository siteRepository;
  @Mock private CompanyRuleRepository companyRuleRepository;
  @Mock private UserRestrictedSiteRepository userRestrictedSiteRepository;
  @Mock private LoginEntitlementCorrectionHandler loginEntitlementCorrectionHandler;
  @Mock private SecurityContext securityContext;

  @Mock private DefaultOidcUser defaultOidcUser;

  private FormLoginUserContext formLoginUserContext;

  @BeforeEach
  void setUp() {
    formLoginUserContext =
        new FormLoginUserContext(
            positionRepository,
            siteRepository,
            companyRuleRepository,
            userRestrictedSiteRepository,
            loginEntitlementCorrectionHandler);
  }

  @Test
  void shouldThrowExceptionOnGettingCurrentUserIfAuthenticationIsNull() {
    // given
    SecurityContextHolder.setContext(securityContext);
    // when
    when(securityContext.getAuthentication()).thenReturn(null);
    // then
    var exception =
        assertThrows(GenevaSecurityException.class, () -> formLoginUserContext.getCurrentUser());
    assertEquals(SecurityErrorCodes.SECURITY_BAD_PRINCIPAL, exception.getErrorCode());
  }

  @Test
  void shouldReturnValidRequest() {
    // given
    SecurityContextHolder.setContext(securityContext);
    Authentication authentication = mock(Authentication.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SpringUserDetails springUserDetails = mock(SpringUserDetails.class);
    when(authentication.getPrincipal()).thenReturn(springUserDetails);
    when(loginEntitlementCorrectionHandler.correctEntitlements(springUserDetails))
        .thenReturn(springUserDetails);

    // when
    SpringUserDetails result = formLoginUserContext.getCurrentUser();

    // then
    assertEquals(result, springUserDetails);
    verify(loginEntitlementCorrectionHandler).correctEntitlements(springUserDetails);
  }

  @Test
  void shouldReturnValidOauth2Request() {
    // given
    SecurityContextHolder.setContext(securityContext);
    Authentication authentication = mock(Authentication.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SpringUserDetails springUserDetails = mock(SpringUserDetails.class);
    when(authentication.getPrincipal()).thenReturn(defaultOidcUser);
    when(defaultOidcUser.getAttribute(anyString())).thenReturn(springUserDetails);
    when(loginEntitlementCorrectionHandler.correctEntitlements(springUserDetails))
        .thenReturn(springUserDetails);

    // when
    SpringUserDetails result = formLoginUserContext.getCurrentUser();

    // then
    assertEquals(result, springUserDetails);
    verify(loginEntitlementCorrectionHandler).correctEntitlements(springUserDetails);
  }
}
