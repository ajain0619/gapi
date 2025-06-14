package com.ssp.geneva.common.security.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.BaseModel;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.util.UserTestData;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
class SpringUserDetailsIT {

  @Test
  void shouldHaveAccessForNexageUserAdminRole() {
    // given
    User testUser = UserTestData.createUser();

    // when
    boolean canAccess =
        new SpringUserDetails(
                new UserAuth(testUser, UserTestData.buildOneCentralEntitlements(testUser)))
            .canAccess(testUser);

    // then
    assertTrue(canAccess);
  }

  @Test
  void shouldHaveAccessForNexageUserAdminRoleWithSellerCompany() {
    // given
    User testUser = UserTestData.createUser();

    // when
    boolean canAccess =
        new SpringUserDetails(
                new UserAuth(testUser, UserTestData.buildOneCentralEntitlements(testUser)))
            .canAccess(testUser.getCompanyPid());

    // then
    assertTrue(canAccess);
  }

  @Test
  void shouldHaveAccessForNexageUserAdminRoleWithSellerSeat() {
    // given
    User testUser =
        UserTestData.createUser(
            User.Role.ROLE_ADMIN,
            UserTestData.createCompany(CompanyType.SELLER),
            UserTestData.createCompany(CompanyType.SELLER));

    // when
    boolean canAccess =
        new SpringUserDetails(
                new UserAuth(testUser, UserTestData.buildOneCentralEntitlements(testUser)))
            .canAccessSellerSeat(testUser.getSellerSeat().getPid());

    // then
    assertTrue(canAccess);
  }

  @Test
  void shouldBeAssociatedWithSellerSeatForNexageUserAdminRoleWithSellerSeats() {
    // given
    User testUser =
        UserTestData.createUser(
            User.Role.ROLE_ADMIN,
            UserTestData.createCompany(CompanyType.SELLER),
            UserTestData.createCompany(CompanyType.SELLER));

    Optional<Company> firstSeller = testUser.getSellerSeat().getSellers().stream().findFirst();
    Long firstCompanyPid = firstSeller.map(BaseModel::getPid).orElse(null);
    if (firstCompanyPid != null) {

      // when
      boolean canAccess =
          new SpringUserDetails(
                  new UserAuth(testUser, UserTestData.buildOneCentralEntitlements(testUser)))
              .isAssociatedWithSellerSeat(Set.of(firstCompanyPid));

      // then
      assertTrue(canAccess);
    }
  }

  @Test
  void shouldHaveAccessForSellerSeatIdsForNexageUserAdminRoleWithSellerSeats() {
    // given
    User testUser =
        UserTestData.createUser(
            User.Role.ROLE_ADMIN,
            UserTestData.createCompany(CompanyType.SELLER),
            UserTestData.createCompany(CompanyType.SELLER));

    Optional<Company> firstSeller = testUser.getSellerSeat().getSellers().stream().findFirst();
    Long firstCompanyPid = firstSeller.map(BaseModel::getPid).orElse(null);
    if (firstCompanyPid != null) {

      // when
      boolean canAccess =
          new SpringUserDetails(
                  new UserAuth(testUser, UserTestData.buildOneCentralEntitlements(testUser)))
              .canAccess(Set.of(firstCompanyPid));

      // then
      assertTrue(canAccess);
    }
  }

  @Test
  void shouldMatchCompanyPidForUserCompanyPid() {
    // given
    User testUser =
        UserTestData.createUser(
            User.Role.ROLE_ADMIN,
            UserTestData.createCompany(CompanyType.SELLER),
            UserTestData.createCompany(CompanyType.SELLER));

    // when
    Set<Long> companyPids =
        new SpringUserDetails(
                new UserAuth(testUser, UserTestData.buildOneCentralEntitlements(testUser)))
            .getCompanyPids();

    // then
    assertEquals(companyPids.size(), testUser.getCompanies().size());
    assertEquals(
        companyPids,
        testUser.getCompanies().stream().map(BaseModel::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldReturnFalseForCompanyIsNotPublisherSelfServeEnabled() {
    // given
    User testUser =
        UserTestData.createUser(
            User.Role.ROLE_ADMIN,
            UserTestData.createCompany(CompanyType.SELLER),
            UserTestData.createCompany(CompanyType.SELLER));

    Optional<Company> firstCompany = testUser.getCompanies().stream().findFirst();
    Long firstCompanyPid = firstCompany.map(BaseModel::getPid).orElse(null);

    if (firstCompanyPid != null) {

      // when
      boolean isPublisherSelfServerEnabled =
          new SpringUserDetails(
                  new UserAuth(testUser, UserTestData.buildOneCentralEntitlements(testUser)))
              .isPublisherSelfServeEnabled(firstCompanyPid);

      // then
      assertFalse(isPublisherSelfServerEnabled);
    }
  }

  @Test
  void shouldReturnTrueForPudEqualsUserPid() {
    // given
    User testUser = UserTestData.createUser();

    // when
    Long actualPid =
        new SpringUserDetails(
                new UserAuth(testUser, UserTestData.buildOneCentralEntitlements(testUser)))
            .getPid();

    // then
    assertEquals(testUser.getPid(), actualPid);
  }
}
