package com.nexage.app.util.validator;

import static com.nexage.admin.core.model.User.Role.ROLE_API;
import static com.nexage.admin.core.model.User.Role.ROLE_MANAGER_SMARTEX;
import static com.nexage.admin.core.model.User.Role.ROLE_MANAGER_YIELD;
import static com.nexage.admin.core.model.User.Role.ROLE_USER;
import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatDTO;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatUser;
import static com.nexage.app.web.support.TestObjectsFactory.createUser;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.nexage.app.dto.user.CompanyViewDTO;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.CompanyViewDTOMapper;
import com.nexage.app.mapper.UserDTOMapper;
import com.nexage.app.services.SellerLimitService;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.error.model.ErrorCode;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

  @Mock private SellerLimitService sellerLimitService;
  @Mock private ConstraintValidatorContext context;
  @InjectMocks @Spy UserValidatorTestTarget userValidator;

  /** Override target class to avoid dealing with final MessageHandler class */
  static class UserValidatorTestTarget extends UserValidator {

    public UserValidatorTestTarget(
        SellerLimitService sellerLimitService, MessageHandler messageHandler) {
      super(sellerLimitService, messageHandler);
    }

    @Override
    boolean addConstraintMessage(
        ConstraintValidatorContext context, String field, ErrorCode errorCode) {
      return false;
    }
  }

  @Test
  void testIsValid() {
    // given
    UserDTO user = UserDTOMapper.MAPPER.map(createSellerSeatUser());
    // when & then
    assertTrue(userValidator.isValid(user, context));
  }

  @Test
  void testValidateUserCompaniesStateWhenValidationFails() {
    // given
    UserDTO user = new UserDTO();
    user.setCompanies(new HashSet<>());
    // when
    userValidator.validateUserCompaniesState(user, context);
    // then
    verify(userValidator)
        .addConstraintMessage(
            context,
            "companies",
            ServerErrorCodes.SERVER_CREATE_USER_MISSING_COMPANY_OR_SELLER_SEAT);
  }

  @Test
  void testValidateUserCompaniesStateWhenValidationPasses() {
    // given
    UserDTO user = new UserDTO();
    user.setSellerSeat(createSellerSeatDTO(12345L, true));
    // when & then
    assertTrue(userValidator.validateUserCompaniesState(user, context));
  }

  @Test
  void testValidateSellerSeatLimitWhenValidationFails() {
    // given
    UserDTO user = new UserDTO();
    user.setCompanies(Set.of(CompanyViewDTO.builder().build()));
    // when
    userValidator.validateSellerSeatLimit(user, context);
    // then
    verify(userValidator)
        .addConstraintMessage(context, "sellerSeat", ServerErrorCodes.SERVER_LIMIT_REACHED);
  }

  @Test
  void testValidateSellerSeatLimitWhenValidationPass() {
    UserDTO user = UserDTOMapper.MAPPER.map(createUser());
    assertTrue(userValidator.validateSellerSeatLimit(user, context));
  }

  @Test
  void testValidateSellerSeatLimitWhenValidationPasses() {
    // given
    UserDTO user = new UserDTO();
    user.setSellerSeat(createSellerSeatDTO(12345L, true));
    // when & then
    assertTrue(userValidator.validateSellerSeatLimit(user, context));
  }

  @Test
  void testValidatePrimaryContactEnabledWhenValidationFails() {
    // given
    UserDTO user = new UserDTO();
    user.setPrimaryContact(true);
    user.setEnabled(false);
    // when
    userValidator.validatePrimaryContactEnabled(user, context);
    // then
    verify(userValidator)
        .addConstraintMessage(
            context, "primaryContact", ServerErrorCodes.SERVER_PRIMARY_CONTACT_NOT_ENABLED);
  }

  @Test
  void testValidatePrimaryContactEnabledWhenValidationPasses() {
    // given
    UserDTO user = new UserDTO();
    user.setPrimaryContact(false);
    // when & then
    assertTrue(userValidator.validatePrimaryContactEnabled(user, context));
  }

  @Test
  void testValidateRoleWhenValidationFailsForROLE_API() {
    // given
    UserDTO user = new UserDTO();
    user.setRole(ROLE_API);
    CompanyViewDTO company = CompanyViewDTOMapper.MAPPER.map(createCompany(CompanyType.SELLER));
    user.setCompanies(Set.of(company));
    // when
    userValidator.validateRole(user, context);
    // then
    verify(userValidator)
        .addConstraintMessage(context, "role", ServerErrorCodes.SERVER_INVALID_CONTACT_EMAIL);
  }

  @Test
  void testValidateRoleWhenValidationFailsForROLE_MANAGER_YIELD() {
    // given
    UserDTO user = new UserDTO();
    user.setRole(ROLE_MANAGER_YIELD);
    CompanyViewDTO company = CompanyViewDTOMapper.MAPPER.map(createCompany(CompanyType.SELLER));
    user.setCompanies(Set.of(company));
    // when
    userValidator.validateRole(user, context);
    // then
    verify(userValidator)
        .addConstraintMessage(context, "role", ServerErrorCodes.SERVER_ROLE_COMPANY_TYPE_MISMATCH);
  }

  @Test
  void testValidateRoleWhenValidationFailsForROLE_MANAGER_SMARTEX() {
    // given
    UserDTO user = new UserDTO();
    user.setRole(ROLE_MANAGER_SMARTEX);
    CompanyViewDTO company = CompanyViewDTOMapper.MAPPER.map(createCompany(CompanyType.SELLER));
    user.setCompanies(Set.of(company));
    // when
    userValidator.validateRole(user, context);
    // then
    verify(userValidator)
        .addConstraintMessage(context, "role", ServerErrorCodes.SERVER_ROLE_COMPANY_TYPE_MISMATCH);
  }

  @Test
  void testValidateRoleWhenValidationPasses() {
    // given
    UserDTO user = new UserDTO();
    user.setRole(ROLE_USER);
    // when & then
    assertTrue(userValidator.validateRole(user, context));
  }

  @Test
  void shouldNotFailOnManagerYieldRoleWhenNexageCompanyTypeIsIncluded() {
    // given
    UserDTO user = new UserDTO();
    user.setRole(ROLE_MANAGER_YIELD);
    user.setCompanies(Set.of(CompanyViewDTO.builder().type(CompanyType.NEXAGE).build()));
    // when
    userValidator.validateRole(user, context);
    // then
    verify(userValidator, never())
        .addConstraintMessage(context, "role", ServerErrorCodes.SERVER_ROLE_COMPANY_TYPE_MISMATCH);

    user.setCompanies(Set.of(CompanyViewDTO.builder().pid(1L).build()));
    // when
    userValidator.validateRole(user, context);
    // then
    verify(userValidator, never())
        .addConstraintMessage(context, "role", ServerErrorCodes.SERVER_ROLE_COMPANY_TYPE_MISMATCH);
  }
}
