package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.CompanyView;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/company-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class CompanyViewRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private CompanyViewRepository companyViewRepository;

  @Test
  void shouldReturnTrueWhenCompanyIdsExists() {
    // given
    var pids = Set.of(1L, 2L, 3L);

    // then
    assertTrue(companyViewRepository.existsByPidIn(pids, 3L));
  }

  @Test
  void shouldReturnFalseWhenCompanyIdsDoNotExists() {
    // given
    Set<Long> pids = Set.of(1L, 2L, 23L, 24L);

    // then
    assertFalse(companyViewRepository.existsByPidIn(pids, 4L));
  }

  @Test
  void shouldReturnTrueWhenCompanyListMatchesGivenType() {
    // given
    Set<Long> pidList = Set.of(2L, 3L);

    // then
    assertTrue(companyViewRepository.matchCompanyTypeWith(pidList, 2L, CompanyType.SELLER));
  }

  @Test
  void shouldReturnFalseWhenCompanyListNotMatchesGivenType() {
    // given
    Set<Long> pidList = Set.of(1L, 2L, 3L);

    // then
    assertFalse(companyViewRepository.matchCompanyTypeWith(pidList, 3L, CompanyType.SELLER));
  }

  @Test
  void shouldReturnFalseWhenCompanyAssociatedToValidSeatAndInputInvalid() {
    // given
    Long sellerPid = 1L;
    Long sellerSeatPid = 3L;

    // then
    assertFalse(companyViewRepository.isSellerAssociatedWithSellerSeat(sellerPid, sellerSeatPid));
  }

  @Test
  void shouldReturnTrueWhenCompanyAssociatedToValidSeatAndValidInput() {
    // given
    Long sellerPid = 1L;
    Long sellerSeatPid = 2L;

    // then
    assertTrue(companyViewRepository.isSellerAssociatedWithSellerSeat(sellerPid, sellerSeatPid));
  }

  @Test
  void shouldReturnAllCompaniesWhenTypeIsSeller() {
    // given
    CompanyView publisher1 = new CompanyView(2L, "Publisher 1", null, false);
    CompanyView publisher2 = new CompanyView(3L, "Seller 1", null, false);
    CompanyView publisher3 = new CompanyView(4L, "Test 4", null, false);
    CompanyView publisher4 = new CompanyView(6L, "Nexage Inc 6", null, false);
    List<CompanyView> expectedResult = List.of(publisher1, publisher2, publisher3, publisher4);
    // when
    List<CompanyView> companiesByType =
        companyViewRepository.findCompaniesByType(CompanyType.SELLER);
    // then
    validateCompanyList(expectedResult, companiesByType);
  }

  private void validateCompanyList(
      List<CompanyView> expectedResult, List<CompanyView> companiesByType) {
    assertEquals(expectedResult.size(), companiesByType.size());
    for (int i = 0; i < companiesByType.size(); i++) {
      assertEquals(expectedResult.get(i).getPid(), companiesByType.get(i).getPid());
      assertEquals(expectedResult.get(i).getName(), companiesByType.get(i).getName());
      assertEquals(
          expectedResult.get(i).getGlobalAliasName(), companiesByType.get(i).getGlobalAliasName());
    }
  }
}
