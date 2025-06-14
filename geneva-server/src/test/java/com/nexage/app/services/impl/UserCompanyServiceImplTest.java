package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static com.nexage.app.web.support.TestObjectsFactory.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.user.CompanyViewDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCompanyServiceImplTest {

  @Mock private CompanyRepository companyRepository;
  @InjectMocks private UserCompanyServiceImpl companyService;

  @Test
  void shouldThrowExceptionOnSetupWhereCompanyNotFound() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    User user = createUser(User.Role.ROLE_ADMIN, company);
    when(companyRepository.findById(company.getPid())).thenReturn(Optional.empty());

    var companies = user.getCompanies();
    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> companyService.updateUserWithVerifiedCompany(user, companies, Company::getPid));
    assertEquals(ServerErrorCodes.SERVER_CREATE_USER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnUpdateWhereCompanyNotSpecified() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    User user = createUser(User.Role.ROLE_ADMIN, company);

    // when & then
    Set<CompanyViewDTO> companyViews = Set.of();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                companyService.updateUserWithVerifiedCompany(
                    user, companyViews, CompanyViewDTO::getPid));
    assertEquals(
        ServerErrorCodes.SERVER_CREATE_USER_MISSING_COMPANY_OR_SELLER_SEAT,
        exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnUpdateWhereCompanyNotFound() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    User user = createUser(User.Role.ROLE_ADMIN, company);
    when(companyRepository.findById(company.getPid())).thenReturn(Optional.empty());

    // when & then
    Set<CompanyViewDTO> companyViews =
        Set.of(new CompanyViewDTO(company.getPid(), "name", CompanyType.SELLER, false));
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                companyService.updateUserWithVerifiedCompany(
                    user, companyViews, CompanyViewDTO::getPid));
    assertEquals(ServerErrorCodes.SERVER_CREATE_USER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void updateUserWhereUserIsGlobal() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    User user = createUser(User.Role.ROLE_ADMIN, company);
    user.setGlobal(true);

    var companies = user.getCompanies();

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> companyService.updateUserWithVerifiedCompany(user, companies, Company::getPid));
    assertEquals(
        ServerErrorCodes.SERVER_ONLY_SELLER_SEAT_USER_CAN_BE_GLOBAL, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenMultipleCompanies() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    Company company2 = createCompany(CompanyType.SELLER);
    User user = createUser(User.Role.ROLE_ADMIN, company);
    user.addCompany(company2);

    var companies = user.getCompanies();
    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> companyService.updateUserWithVerifiedCompany(user, companies, Company::getPid));
    assertEquals(ServerErrorCodes.SERVER_CREATE_USER_MULTIPLE_COMPANIES, exception.getErrorCode());
  }

  @Test
  void updateUser() {
    // given
    Company companyIn = createCompany(CompanyType.SELLER);
    Company companyOut = createCompany(CompanyType.SELLER);
    User user = createUser(User.Role.ROLE_ADMIN, companyIn);
    when(companyRepository.findById(companyIn.getPid())).thenReturn(Optional.of(companyOut));

    // when
    companyService.updateUserWithVerifiedCompany(user, user.getCompanies(), Company::getPid);

    // then
    assertEquals(1, user.getCompanies().size());
    assertSame(user.getCompanies().stream().findFirst().get(), companyOut);
  }
}
