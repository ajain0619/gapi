package com.nexage.app.util.validator;

import static com.nexage.admin.core.model.User.Role.ROLE_MANAGER_SMARTEX;
import static com.nexage.admin.core.model.User.Role.ROLE_MANAGER_YIELD;
import static com.nexage.admin.core.model.User.Role.ROLE_USER;
import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatUser;
import static com.nexage.app.web.support.TestObjectsFactory.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.app.dto.SellerSeatDTO;
import com.nexage.app.dto.user.CompanyViewDTO;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.CompanyViewDTOMapper;
import com.nexage.app.mapper.UserDTOMapper;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.error.model.ErrorCode;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserUpdateValidatorTest {

  @InjectMocks @Spy UserUpdateValidatorTestTarget userValidator;
  @Mock private UserContext userContext;
  @Mock private ConstraintValidatorContext context;

  static class UserUpdateValidatorTestTarget extends UserUpdateValidator {

    public UserUpdateValidatorTestTarget(UserContext userContext, MessageHandler messageHandler) {
      super(userContext, messageHandler);
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
    when(userContext.hasAccessToSellerSeatOrHasNexageAffiliation(anyLong())).thenReturn(true);
    // when & then
    assertTrue(userValidator.isValid(user, context));
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
  void shouldNotFailOnManagerSmartExchangeRoleWhenNexageCompanyTypeIsIncluded() {
    // given
    UserDTO user = new UserDTO();
    user.setRole(ROLE_MANAGER_SMARTEX);
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

  @Test
  void testValidateGlobalFlagOnlyForSellerSeatUser() {
    // given
    UserDTO userDto = new UserDTO();
    userDto.setGlobal(true);

    // when
    userValidator.validateUserAsSellerSeat(userDto, context);

    // then
    verify(userValidator)
        .addConstraintMessage(
            context, "sellerSeat", ServerErrorCodes.SERVER_ONLY_SELLER_SEAT_USER_CAN_BE_GLOBAL);
  }

  @Test
  void testValidateUserAsSellerSeatSuccess() {
    // given
    UserDTO userDto = new UserDTO();
    userDto.setGlobal(false);
    SellerSeatDTO ss = new SellerSeatDTO();
    ss.setPid(1L);
    userDto.setSellerSeat(ss);

    // when and then
    userValidator.validateRole(userDto, context);
  }

  @Test
  void shouldFailOnSellerSeatUserUpdateHavingWrongAffiliationThenThrowException() {
    // given
    UserDTO userDto = new UserDTO();
    userDto.setGlobal(false);
    SellerSeatDTO ss = new SellerSeatDTO();
    ss.setPid(1L);
    userDto.setSellerSeat(ss);

    // when
    userValidator.validateUserAsSellerSeat(userDto, context);

    // then
    verify(userValidator)
        .addConstraintMessage(context, "sellerSeat", SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
  }

  @Test
  void shouldFailOnSellerSeatUserUpdateHavingInvalidPid() {
    // given
    UserDTO userDto = new UserDTO();
    userDto.setGlobal(false);
    userDto.setSellerSeat(new SellerSeatDTO());

    // when
    userValidator.validateUserAsSellerSeat(userDto, context);

    // then
    verify(userValidator)
        .addConstraintMessage(context, "sellerSeat", SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
  }

  @Test
  void testValidateRoleWhenValidationPasses() {
    // given
    UserDTO user = new UserDTO();

    // when
    user.setRole(ROLE_USER);

    // then
    assertTrue(userValidator.validateRole(user, context));
  }

  @Test
  void updateUser_disableDealAdminWithWrongRoleThrowException() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);

    User user = createTestUser(company);
    user.setDealAdmin(false);

    User oldUser = (User) SerializationUtils.clone(user);
    oldUser.setDealAdmin(true);

    when(userContext.isNexageAdmin()).thenReturn(false);

    // when
    UserDTO userDto = UserDTOMapper.MAPPER.map(user);
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> userValidator.validateEmail(userDto, oldUser));
    // then
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void updateUser_enableDealAdminWithWrongRoleThrowException() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);

    User user = createTestUser(company);
    user.setDealAdmin(true);

    User oldUser = (User) SerializationUtils.clone(user);
    oldUser.setDealAdmin(false);

    // when
    UserDTO userDto = UserDTOMapper.MAPPER.map(user);
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> userValidator.validateEmail(userDto, oldUser));

    // then
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void updateUser_setEmailWithWrongRoleThrowException() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);
    User user = createTestUser(company);
    when(userContext.isCurrentUser(anyLong())).thenReturn(false);
    when(userContext.isNexageAdmin()).thenReturn(false);
    when(userContext.isOcApiIIQ()).thenReturn(false);
    UserDTO userDto = UserDTOMapper.MAPPER.map(user);
    userDto.setEmail("test123@test.com");

    // when
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> userValidator.validateEmail(userDto, user));

    // then
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void updateUser_whenUserPidDontMatch_throwException() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);
    User user = createTestUser(company);
    UserDTO userDto = UserDTOMapper.MAPPER.map(user);
    userDto.setEmail("test123@test.com");
    Long userPid = 100L;
    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> userValidator.validatePid(userDto, userPid));

    // then
    assertEquals(ServerErrorCodes.SERVER_PIDS_MISMATCH, exception.getErrorCode());
  }

  @Test
  void updateUserWithTheRoleOf_ROLE_API_IIQ() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);
    User user = createTestUser(company);
    when(userContext.isOcApiIIQ()).thenReturn(true);
    UserDTO userDto = UserDTOMapper.MAPPER.map(user);
    userDto.setEmail("test123@test.com");

    // when
    userValidator.validateEmail(userDto, user);

    // then
    verify(userValidator, never())
        .addConstraintMessage(context, "role", SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
  }

  private User createTestUser(Company company) {
    User user = createUser(User.Role.ROLE_ADMIN, company);
    user.setEmail("valid@mail.com");
    return user;
  }
}
