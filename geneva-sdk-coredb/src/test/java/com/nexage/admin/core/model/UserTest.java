package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.Sets;
import com.nexage.TestObjectsFactory;
import com.nexage.admin.core.model.User.Role;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class UserTest {

  @Test
  void nonSellerSeatUserWithMultipleCompaniesThrowsISEOnGetType() {
    Company c1 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    Company c2 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, c1, c2);
    user.setSellerSeat(null);

    assertThrows(IllegalStateException.class, () -> user.getCompanyType());
  }

  @Test
  void sellerSeatUserShouldHaveCorrespondingAuthorities() {
    Company c1 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    Company c2 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, c1, c2);

    assertEquals(CompanyType.SELLER, user.getCompanyType());
    assertEquals(
        Sets.newHashSet("ROLE_ADMIN_SELLER", "ROLE_ADMIN_SELLER_SEAT"),
        user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet()));
  }

  @Test
  void aDedicatedMethodToAddCompanyShouldBeProvided() {
    // crate user with one company already assigned
    User user = TestObjectsFactory.createUser();
    assertEquals(1, user.getCompanies().size());
    user.addCompany(TestObjectsFactory.createCompany(CompanyType.BUYER));
    assertEquals(2, user.getCompanies().size());
  }

  @Test
  void aDedicatedMethodToRemoveCompanyShouldBeProvided() {
    // crate user with one company already assigned
    User user = TestObjectsFactory.createUser();
    assertEquals(1, user.getCompanies().size());
    Company company = user.getCompanies().iterator().next();
    user.removeCompany(company);
    assertEquals(0, user.getCompanies().size());
  }

  @Test
  void shouldReturnEmptyCompanyMdmIdsByDefault() {
    // Given
    Company c1 = TestObjectsFactory.createCompany(CompanyType.SELLER);

    // Then
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, c1);

    // Then
    Set<String> companyMdmIds = user.getCompanyMdmIds();
    assertNotNull(companyMdmIds);
    assertTrue(companyMdmIds.isEmpty());
  }

  @Test
  void shouldReturnEmptySellerSeatMdmIdsByDefault() {
    // Given
    Company c1 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    Company c2 = TestObjectsFactory.createCompany(CompanyType.SELLER);

    // When
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, c1, c2);

    // Then
    Set<String> companyMdmIds = user.getCompanyMdmIds();
    assertNotNull(companyMdmIds);
    assertTrue(companyMdmIds.isEmpty());

    Set<String> sellerSeatMdmIds = user.getSellerSeatMdmIds();
    assertNotNull(sellerSeatMdmIds);
    assertTrue(sellerSeatMdmIds.isEmpty());
  }

  @Test
  void shouldExtractCompanyMdmIdsFromAllCompanies() {
    // Given
    Company c1 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    c1.setMdmIds(createCompanyMdmIds(c1, "123456", "987"));

    Company c2 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    c2.setMdmIds(createCompanyMdmIds(c2, "123", "987"));

    // When
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, c1, c2);
    SellerSeat sellerSeat = user.getSellerSeat();
    sellerSeat.setMdmIds(createSellerSeatMdmIds(sellerSeat, "12"));

    // Then
    Set<String> companyMdmIds = user.getCompanyMdmIds();
    assertNotNull(companyMdmIds);
    assertFalse(companyMdmIds.isEmpty());
    assertEquals(3, companyMdmIds.size(), "Wrong mdmId count for companies");

    Set<String> sellerSeatMdmIds = user.getSellerSeatMdmIds();
    assertNotNull(sellerSeatMdmIds);
    assertFalse(sellerSeatMdmIds.isEmpty());
    assertEquals(1, sellerSeatMdmIds.size(), "Wrong seller seat mdmId count");
  }

  @Test
  void shouldReturnEmptySellerSeatMdmIdsForNonSellerSeatUserWithMultipleCompanies() {
    // Given
    Company c1 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    c1.setMdmIds(createCompanyMdmIds(c1, "123456", "987"));

    Company c2 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    c2.setMdmIds(createCompanyMdmIds(c2, "123", "987"));

    // When
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, c1, c2);
    user.setSellerSeat(null);

    // Then
    Set<String> companyMdmIds = user.getCompanyMdmIds();
    assertNotNull(companyMdmIds);
    assertFalse(companyMdmIds.isEmpty());
    assertEquals(3, companyMdmIds.size(), "Wrong mdmId count for companies");

    Set<String> sellerSeatMdmIds = user.getSellerSeatMdmIds();
    assertNotNull(sellerSeatMdmIds);
    assertTrue(sellerSeatMdmIds.isEmpty());
  }

  @Test
  void shouldReturnOnlySellerSeatMdmIdsForSellerSeatUser() {
    // Given
    Company c1 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    Company c2 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, c1, c2);

    SellerSeat sellerSeat = user.getSellerSeat();

    // When
    sellerSeat.setMdmIds(createSellerSeatMdmIds(sellerSeat, "12"));

    // Then
    Set<String> companyMdmIds = user.getCompanyMdmIds();
    assertNotNull(companyMdmIds);
    assertTrue(companyMdmIds.isEmpty());

    Set<String> sellerSeatMdmIds = user.getSellerSeatMdmIds();
    assertNotNull(sellerSeatMdmIds);
    assertFalse(sellerSeatMdmIds.isEmpty());
    assertEquals(1, sellerSeatMdmIds.size());
  }

  private List<CompanyMdmId> createCompanyMdmIds(final Company c, String... mdmIds) {
    return Arrays.stream(mdmIds)
        .map(
            mdmId -> {
              var mdm = new CompanyMdmId(c);
              mdm.setId(mdmId);
              return mdm;
            })
        .collect(Collectors.toList());
  }

  private List<SellerSeatMdmId> createSellerSeatMdmIds(final SellerSeat seat, String... mdmIds) {
    return Arrays.stream(mdmIds)
        .map(
            mdmId -> {
              var mdm = new SellerSeatMdmId(seat);
              mdm.setId(mdmId);
              return mdm;
            })
        .collect(Collectors.toList());
  }

  @Test
  void shouldReturnSellerSeatPidWhenSellerSeatIsNotNull() {
    // given
    User user = TestObjectsFactory.createUser();
    SellerSeat sellerSeat = TestObjectsFactory.createSellerSeat();
    sellerSeat.setPid(234L);
    user.setSellerSeat(sellerSeat);

    // when
    Long returnedPid = user.getSellerSeatPid();

    // then
    assertEquals(sellerSeat.getPid(), returnedPid);
  }

  @Test
  void shouldReturnNullSellerSeatPidWhenSellerSeatIsNull() {
    // given
    User user = TestObjectsFactory.createUser();

    // when
    Long returnedPid = user.getSellerSeatPid();

    // then
    assertNull(returnedPid);
  }
}
