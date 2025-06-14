package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.User;
import com.ssp.geneva.common.model.inventory.CompanyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InternalUserValidatorTest {

  @Test
  void shouldBeValidInternalUser() {
    User user = mock(User.class);
    when(user.getEmail()).thenReturn("john.doe@yahooinc.com");
    when(user.getCompanyType()).thenReturn(CompanyType.NEXAGE);
    assertTrue(InternalUserValidator.isInternal(user));
  }

  @Test
  void shouldNotBeValidInternalUser() {
    User user = mock(User.class);
    when(user.getEmail()).thenReturn("john.doe@aol.com");
    when(user.getCompanyType()).thenReturn(CompanyType.SELLER);
    assertFalse(InternalUserValidator.isInternal(user));

    when(user.getEmail()).thenReturn("john.doe@yahoo.com");
    assertFalse(InternalUserValidator.isInternal(user));

    when(user.getCompanyType()).thenReturn(CompanyType.BUYER);
    assertFalse(InternalUserValidator.isInternal(user));

    when(user.getCompanyType()).thenReturn(CompanyType.SEATHOLDER);
    assertFalse(InternalUserValidator.isInternal(user));
  }

  @Test
  void shouldNotBeValidInternalUserWhenTypeIsNotTheExpected() {
    User user = mock(User.class);
    when(user.getEmail()).thenReturn("john.doe@yahooinc.com");
    when(user.getCompanyType()).thenReturn(CompanyType.SELLER);
    assertFalse(InternalUserValidator.isInternal(user));

    when(user.getCompanyType()).thenReturn(CompanyType.BUYER);
    assertFalse(InternalUserValidator.isInternal(user));

    when(user.getCompanyType()).thenReturn(CompanyType.SEATHOLDER);
    assertFalse(InternalUserValidator.isInternal(user));
  }
}
