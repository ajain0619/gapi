package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.model.UserRestrictedSite;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.admin.core.repository.UserRestrictedSiteRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  private static final long COMPANY_PID = 111L;

  @Mock private CompanyRepository companyRepository;
  @Mock private SiteRepository siteRepository;
  @Mock private UserContext userContext;
  @Mock private UserRepository userRepository;
  @Mock private UserRestrictedSiteRepository userRestrictedSiteRepository;
  @InjectMocks private UserServiceImpl userService;

  @Test
  void whenGettingUsersByCompanyPidFailIfCompanyNotFound() {
    // given
    given(companyRepository.existsById(COMPANY_PID)).willReturn(false);

    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> userService.getAllUsersByCompanyPid(COMPANY_PID));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void whenGettingUsersByCompanyPidFailIfCurrentUserCannotAccessIt() {
    // given
    given(companyRepository.existsById(COMPANY_PID)).willReturn(true);
    given(userContext.doSameOrNexageAffiliation(COMPANY_PID)).willReturn(false);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> userService.getAllUsersByCompanyPid(COMPANY_PID));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void whenGettingUsersByCompanyPidThanSucceed() {
    // given
    given(companyRepository.existsById(COMPANY_PID)).willReturn(true);
    given(userContext.doSameOrNexageAffiliation(COMPANY_PID)).willReturn(true);
    Company company = createCompany(CompanyType.SELLER);
    User user = createUser(company);

    List<User> found = Collections.singletonList(user);
    given(userRepository.findAll(any(Specification.class))).willReturn(found);

    // when
    List<User> users = userService.getAllUsersByCompanyPid(COMPANY_PID);

    // then
    assertEquals(1, users.size());
  }

  @Test
  void shouldRestrictUserAccessToSites() {
    // given
    long sitePid = 11L;
    Company company = new Company();
    company.setPid(COMPANY_PID);
    given(siteRepository.findCompanyPidByPid(sitePid)).willReturn(COMPANY_PID);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));
    given(userContext.doSameOrNexageAffiliation(user)).willReturn(true);

    // when
    userService.restrictUserAccessToSites(user.getPid(), List.of(sitePid));

    // then
    ArgumentCaptor<UserRestrictedSite> argumentCaptor =
        ArgumentCaptor.forClass(UserRestrictedSite.class);
    verify(userRestrictedSiteRepository).save(argumentCaptor.capture());
    assertEquals(user.getPid(), argumentCaptor.getValue().getPk().getUserId());
    assertEquals(sitePid, argumentCaptor.getValue().getPk().getSiteId());
  }

  @Test
  void shouldThrowUnauthorizedWhenNexageAffiliationIsFalseForRestrictSites() {
    // when
    when(userContext.doSameOrNexageAffiliation(any(User.class))).thenReturn(false);

    // given
    long sitePid = 11L;
    Company company = new Company();
    company.setPid(COMPANY_PID);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));

    // then
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> userService.restrictUserAccessToSites(user.getPid(), List.of(sitePid)));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenRestrictUserAccessToSiteWithDifferentCompanyPid() {
    // given
    long sitePid = 11L;
    Company company = new Company();
    company.setPid(COMPANY_PID);
    given(siteRepository.findCompanyPidByPid(sitePid)).willReturn(123L);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));
    given(userContext.doSameOrNexageAffiliation(user)).willReturn(true);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> userService.restrictUserAccessToSites(user.getPid(), List.of(sitePid)));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_USER_RESTRICTION_ON_INVALID_SITE, exception.getErrorCode());
  }

  @Test
  void shouldThrowResourceNotFoundExceptionWhenRestrictUserAccessToNonExistingSite() {
    // given
    long sitePid = 11L;
    Company company = new Company();
    company.setPid(COMPANY_PID);
    given(siteRepository.findCompanyPidByPid(sitePid)).willReturn(null);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));
    given(userContext.doSameOrNexageAffiliation(user)).willReturn(true);

    // when
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> userService.restrictUserAccessToSites(user.getPid(), List.of(sitePid)));

    // then
    assertEquals(ServerErrorCodes.SERVER_SITE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldAllowUserAccessToSites() {
    // given
    long sitePid = 11L;
    Company company = new Company();
    company.setPid(COMPANY_PID);
    given(siteRepository.findCompanyPidByPid(sitePid)).willReturn(COMPANY_PID);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));
    given(userContext.doSameOrNexageAffiliation(user)).willReturn(true);

    // when
    userService.allowUserAccessToSites(user.getPid(), List.of(sitePid));

    // then
    verify(userRestrictedSiteRepository).deleteByPkUserIdAndPkSiteId(user.getPid(), sitePid);
  }

  @Test
  void shouldThrowUnauthorizedWhenNexageAffiliationIsFalseForAllowSites() {
    // when
    when(userContext.doSameOrNexageAffiliation(any(User.class))).thenReturn(false);

    // given
    long sitePid = 11L;
    Company company = new Company();
    company.setPid(COMPANY_PID);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));

    // then
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> userService.allowUserAccessToSites(user.getPid(), List.of(sitePid)));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenAllowUserAccessToSiteWithDifferentCompanyPid() {
    // given
    long sitePid = 11L;
    Company company = new Company();
    company.setPid(COMPANY_PID);
    given(siteRepository.findCompanyPidByPid(sitePid)).willReturn(123L);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));
    given(userContext.doSameOrNexageAffiliation(user)).willReturn(true);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> userService.allowUserAccessToSites(user.getPid(), List.of(sitePid)));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_REVOKE_USER_RESTRICTION_ON_INVALID_SITE, exception.getErrorCode());
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionOnChangePasswordForLocalUser() {
    // given
    Company company = new Company();
    company.setPid(COMPANY_PID);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));
    given(userContext.getPid()).willReturn(user.getPid());
    given(userContext.doSameOrNexageAffiliation(user)).willReturn(true);

    // when && then
    assertThrows(
        UnsupportedOperationException.class,
        () -> userService.changePassword(user.getPid(), user.getPassword(), "newPassword"));
  }

  @Test
  void shouldThrowUnauthorizedOnUserPidMismatchForChangePassword() {
    // given
    given(userContext.getPid()).willReturn(1L);

    // then
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> userService.changePassword(2L, "oldPassword", "newPassword"));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldDeleteUser() {
    // given
    Company company = new Company();
    company.setPid(COMPANY_PID);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));
    given(userContext.isCurrentUser(user.getPid())).willReturn(false);
    given(userContext.writePrivilegeCheck(user)).willReturn(true);

    // when
    userService.deleteUser(user.getPid());

    // then
    verify(userRestrictedSiteRepository).deleteByPkUserId(user.getPid());
    verify(userRepository).delete(user);
  }

  @Test
  void shouldThrowUnauthorizedOnDeletingTheCurrentUser() {
    // given
    given(userContext.isCurrentUser(1L)).willReturn(true);

    // then
    var exception = assertThrows(GenevaSecurityException.class, () -> userService.deleteUser(1L));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowUnauthorizedOnDeletingWithoutWritePrivilege() {
    // given
    Company company = new Company();
    company.setPid(COMPANY_PID);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userContext.isCurrentUser(user.getPid())).willReturn(false);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));
    given(userContext.writePrivilegeCheck(user)).willReturn(false);
    long pid = user.getPid();

    // then
    var exception = assertThrows(GenevaSecurityException.class, () -> userService.deleteUser(pid));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldGetUser() {
    // given
    Company company = new Company();
    company.setPid(COMPANY_PID);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));
    given(userContext.doSameOrNexageAffiliation(user)).willReturn(true);

    // when
    User result = userService.getUser(user.getPid());

    // then
    assertEquals(user.getPid(), result.getPid());
  }

  @Test
  void shouldThrowUserNotAuthorizedExceptionWhenGetByNotAuthorizedUser() {
    // given
    Company company = new Company();
    company.setPid(COMPANY_PID);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));
    given(userContext.doSameOrNexageAffiliation(user)).willReturn(false);
    long pid = user.getPid();

    // when
    var exception = assertThrows(GenevaSecurityException.class, () -> userService.getUser(pid));

    // then
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowResourceNotFoundExceptionWhenGetNotExistingUser() {
    // given
    long userPid = 1L;
    given(userRepository.findById(userPid)).willReturn(Optional.empty());

    // when
    var exception =
        assertThrows(GenevaValidationException.class, () -> userService.getUser(userPid));

    // then
    assertEquals(CommonErrorCodes.COMMON_USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowResourceNotFoundExceptionWhenDeleteNotExistingUser() {
    // given
    long userPid = 1L;
    given(userRepository.findById(userPid)).willReturn(Optional.empty());
    given(userContext.isCurrentUser(userPid)).willReturn(false);

    // when
    var exception =
        assertThrows(GenevaValidationException.class, () -> userService.deleteUser(userPid));

    // then
    assertEquals(CommonErrorCodes.COMMON_USER_NOT_FOUND, exception.getErrorCode());
  }

  private User createUser(Company company) {
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    user.setEmail("valid@mail.com");
    return user;
  }
}
