package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerSeatRuleServiceImplWriteAccessTest {

  @Mock private UserContext userContext;
  @InjectMocks private SellerSeatRuleServiceImpl service;

  @Test
  void shouldNotAllowWriteAccessForNexageUser() {
    given(userContext.isOcAdminNexage()).willReturn(false);
    given(userContext.isOcManagerNexage()).willReturn(false);
    given(userContext.isOcManagerYieldNexage()).willReturn(false);
    given(userContext.isOcManagerSmartexNexage()).willReturn(false);
    given(userContext.isOcAdminSeller()).willReturn(false);
    given(userContext.isOcManagerSeller()).willReturn(false);
    var exception =
        assertThrows(GenevaSecurityException.class, () -> service.validateWriteAccess());

    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldAllowWriteAccessForNexageAdminUser() {
    given(userContext.isOcAdminNexage()).willReturn(true);

    service.validateWriteAccess();
  }

  @Test
  void shouldAllowWriteAccessForNexageManagerUser() {
    given(userContext.isOcAdminNexage()).willReturn(false);
    given(userContext.isOcManagerNexage()).willReturn(true);

    service.validateWriteAccess();
  }

  @Test
  void shouldAllowWriteAccessForManagerYieldNexageUser() {
    given(userContext.isOcAdminNexage()).willReturn(false);
    given(userContext.isOcManagerNexage()).willReturn(false);
    given(userContext.isOcManagerYieldNexage()).willReturn(true);

    service.validateWriteAccess();
  }

  @Test
  void shouldAllowWriteAccessForManagerSmartexNexageUser() {
    given(userContext.isOcAdminNexage()).willReturn(false);
    given(userContext.isOcManagerNexage()).willReturn(false);
    given(userContext.isOcManagerYieldNexage()).willReturn(false);
    given(userContext.isOcManagerSmartexNexage()).willReturn(true);

    service.validateWriteAccess();
  }

  @Test
  void shouldAllowWriteAccessForAdminSellerUser() {
    given(userContext.isOcAdminNexage()).willReturn(false);
    given(userContext.isOcManagerNexage()).willReturn(false);
    given(userContext.isOcManagerYieldNexage()).willReturn(false);
    given(userContext.isOcManagerSmartexNexage()).willReturn(false);
    given(userContext.isOcAdminSeller()).willReturn(true);

    service.validateWriteAccess();
  }

  @Test
  void shouldAllowWriteAccessForManagerSellerUser() {
    given(userContext.isOcAdminNexage()).willReturn(false);
    given(userContext.isOcManagerNexage()).willReturn(false);
    given(userContext.isOcManagerYieldNexage()).willReturn(false);
    given(userContext.isOcManagerSmartexNexage()).willReturn(false);
    given(userContext.isOcAdminSeller()).willReturn(false);
    given(userContext.isOcManagerSeller()).willReturn(true);

    service.validateWriteAccess();
  }
}
