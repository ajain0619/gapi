package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.User.Role;
import com.nexage.app.dto.user.CompanyViewDTO;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.error.model.ErrorCode;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IdentityIqUserValidatorTest {

  @Mock private UserContext userContext;
  @Mock protected ConstraintValidatorContext ctx;

  @Log4j2
  static class IdentityIqUserValidatorImpl extends IdentityIqUserValidator {

    public IdentityIqUserValidatorImpl(UserContext userContext, MessageHandler messageHandler) {
      super(userContext, messageHandler);
    }

    @Override
    void addConstraintMessage(
        ConstraintValidatorContext context, String field, ErrorCode errorCode) {
      log.debug("do nothing");
    }
  }

  @InjectMocks @Spy private IdentityIqUserValidatorImpl validator;

  @Test
  void shouldBeValidWhenNoInternalUserContext() {
    when(userContext.isOcApiIIQ()).thenReturn(false);
    UserDTO userDTO = new UserDTO();
    assertTrue(validator.isValid(userDTO, ctx));
  }

  @Test
  void shouldBeValidWhenNoIdentityIqRole() {
    when(userContext.isOcApiIIQ()).thenReturn(false);
    UserDTO userDTO = new UserDTO();
    assertTrue(validator.isValid(userDTO, ctx));
  }

  @Test
  void shouldBeValidWhenNoCompanies() {
    when(userContext.isOcApiIIQ()).thenReturn(true);
    UserDTO userDTO = new UserDTO();
    userDTO.setRole(Role.ROLE_ADMIN);
    assertTrue(validator.isValid(userDTO, ctx));
    assertTrue(validator.isValid(userDTO, ctx));
    userDTO.setRole(Role.ROLE_MANAGER);
    assertTrue(validator.isValid(userDTO, ctx));
    userDTO.setRole(Role.ROLE_USER);
    assertTrue(validator.isValid(userDTO, ctx));
    userDTO.setRole(Role.ROLE_MANAGER_YIELD);
    assertTrue(validator.isValid(userDTO, ctx));
  }

  @Test
  void shouldBeValidWithValidRoleWhenCompaniesAreNotChanged() {
    when(userContext.isOcApiIIQ()).thenReturn(true);
    UserDTO userDTO = new UserDTO();
    userDTO.setRole(Role.ROLE_ADMIN);
    userDTO.setCompanies(Collections.emptySet());
    assertTrue(validator.isValid(userDTO, ctx));
    userDTO.setRole(Role.ROLE_MANAGER);
    assertTrue(validator.isValid(userDTO, ctx));
    userDTO.setRole(Role.ROLE_USER);
    assertTrue(validator.isValid(userDTO, ctx));
    userDTO.setRole(Role.ROLE_MANAGER_YIELD);
    assertTrue(validator.isValid(userDTO, ctx));
  }

  @Test
  void shouldBeInvalidForNonInternalCompanies() {
    when(userContext.isOcApiIIQ()).thenReturn(true);
    UserDTO userDTO = new UserDTO();
    userDTO.setCompanies(Set.of(CompanyViewDTO.builder().pid(2L).build()));
    assertFalse(validator.isValid(userDTO, ctx));
  }

  @Test
  void shouldBeInvalidForMissingCompaniesPid() {
    when(userContext.isOcApiIIQ()).thenReturn(true);
    UserDTO userDTO = new UserDTO();
    userDTO.setCompanies(Set.of(CompanyViewDTO.builder().build()));
    assertFalse(validator.isValid(userDTO, ctx));
  }

  @Test
  void shouldBeInvalidForNonInternalCompaniesWithinCollection() {
    when(userContext.isOcApiIIQ()).thenReturn(true);
    UserDTO userDTO = new UserDTO();
    userDTO.setCompanies(
        Set.of(CompanyViewDTO.builder().pid(1L).build(), CompanyViewDTO.builder().pid(2L).build()));
    assertFalse(validator.isValid(userDTO, ctx));
  }

  @Test
  void shouldBeInvalidForInvalidRoleWithValidCompanies() {
    when(userContext.isOcApiIIQ()).thenReturn(true);
    UserDTO userDTO = new UserDTO();
    userDTO.setRole(Role.ROLE_API);
    userDTO.setCompanies(Set.of(CompanyViewDTO.builder().pid(1L).build()));
    assertFalse(validator.isValid(userDTO, ctx));
  }

  @Test
  void shouldBeInvalidWhenCompaniesAreNotChangedButInvalidRole() {
    when(userContext.isOcApiIIQ()).thenReturn(true);
    UserDTO userDTO = new UserDTO();
    userDTO.setRole(Role.ROLE_API);
    userDTO.setCompanies(Collections.emptySet());
    assertFalse(validator.isValid(userDTO, ctx));
  }
}
