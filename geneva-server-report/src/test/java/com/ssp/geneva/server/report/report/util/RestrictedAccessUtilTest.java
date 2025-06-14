package com.ssp.geneva.server.report.report.util;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.SiteRepository;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.server.report.report.ReportUser;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestrictedAccessUtilTest {

  @Mock private SiteRepository siteRepository;
  @InjectMocks private RestrictedAccessUtil restrictedAccessUtil;

  @Test
  void testNoCompaniesPidsGivenAndReportUserIsNexageUser_returnNull() {
    // given
    ReportUser reportUser = ReportUserFactory.aNexageUser();

    // when
    Set<Long> siteIdsRestrictionForCompanies =
        restrictedAccessUtil.getSiteIdsRestrictionForCompanies(emptySet(), reportUser);

    // then
    assertNull(siteIdsRestrictionForCompanies);
  }

  @Test
  void testCompaniesPidsGivenAndReportUserIsNexageUser_returnAllSitesForPidsForCompanies() {
    // given
    ReportUser reportUser = ReportUserFactory.aNexageUser();
    Set<Long> companyPidsToCheck = ImmutableSet.of(1L, 2L);

    given(siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(companyPidsToCheck))
        .willReturn(Set.of(3L, 4L));

    // when
    Set<Long> siteIdsRestrictionForCompanies =
        restrictedAccessUtil.getSiteIdsRestrictionForCompanies(companyPidsToCheck, reportUser);

    // then
    assertEquals(Sets.newHashSet(3L, 4L), siteIdsRestrictionForCompanies);
  }

  @Test
  void noCompaniesGivenAndReportUserIsSellerWithoutUserPid_returnAllSitesAvailableToSeller() {
    // given
    ReportUser reportUser =
        ReportUserFactory.aSeller(
            ReportUserFactory.withCompanies(1L, 2L), ReportUserFactory.noUserPid());

    given(siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(Sets.newHashSet(1L, 2L)))
        .willReturn(Set.of(3L, 4L));

    // when
    Set<Long> siteIdsRestrictionForCompanies =
        restrictedAccessUtil.getSiteIdsRestrictionForCompanies(emptySet(), reportUser);

    // then
    assertEquals(Sets.newHashSet(3L, 4L), siteIdsRestrictionForCompanies);
  }

  @Test
  void noCompaniesGivenAndReportUserIsSeller_returnAllSitesAvailableToReportUser() {
    // given
    ReportUser reportUser = ReportUserFactory.aSeller(ReportUserFactory.withCompanies(1L, 2L));

    given(
            siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
                reportUser.getUserPid(), Sets.newHashSet(5L, 6L)))
        .willReturn(Set.of(3L, 4L));

    // when
    Set<Long> siteIdsRestrictionForCompanies =
        restrictedAccessUtil.getSiteIdsRestrictionForCompanies(Sets.newHashSet(5L, 6L), reportUser);

    // then
    assertEquals(Sets.newHashSet(3L, 4L), siteIdsRestrictionForCompanies);
  }

  @Test
  void shouldThrowExceptionWhenCompanyIsRestricted() {
    // given
    ReportUser reportUser = null;

    // when
    Set resultSet = Sets.newHashSet(5L, 6L);
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> restrictedAccessUtil.getSiteIdsRestrictionForCompanies(resultSet, reportUser));

    // then
    assertEquals("reportUser cannot be null", exception.getMessage());
  }

  @Test
  void testCompanyPidsGivenAndReportUser_returnRestrictedSitesIdsForPidsForCompanies() {
    // given
    ReportUser reportUser = ReportUserFactory.aSeller(ReportUserFactory.withCompanies(1L, 2L));
    Company company = createCompany(CompanyType.NEXAGE);
    given(
            siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
                reportUser.getUserPid(), Set.of(company.getPid())))
        .willReturn(Set.of(1L, 2L));

    // when
    Set<Long> siteIdsRestrictionForCompanies =
        restrictedAccessUtil.getSiteIdsRestrictionForCompany(company.getPid(), reportUser);

    // then
    assertEquals(Sets.newHashSet(1L, 2L), siteIdsRestrictionForCompanies);
  }

  @Test
  void shouldReturnRestrictedSitesIdsForCompanies() {
    // give
    ReportUser reportUser = ReportUserFactory.aSeller(ReportUserFactory.withCompanies());
    Company company = createCompany(CompanyType.SELLER);
    company.setPid(null);

    given(
            siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
                reportUser.getUserPid(), emptySet()))
        .willReturn(Set.of(1L, 2L));

    // when
    Set<Long> siteIdsRestrictionForCompanies =
        restrictedAccessUtil.getSiteIdsRestrictionForCompany(company.getPid(), reportUser);

    // then
    assertEquals(Sets.newHashSet(1L, 2L), siteIdsRestrictionForCompanies);
  }

  @Test
  void noCompaniesGivenAndReportUserIsSellerWithoutUserPid_returnAllSitesAvailableToUser() {
    // given
    ReportUser reportUser = ReportUserFactory.aSeller(ReportUserFactory.withCompanies(1L, 2L));

    given(
            siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
                reportUser.getUserPid(), Sets.newHashSet(1L, 2L)))
        .willReturn(Set.of(3L, 4L));

    // when
    Set<Long> siteIdsRestrictionForCompanies =
        restrictedAccessUtil.getSiteIdsRestrictionForCompanies(emptySet(), reportUser);

    // then
    assertEquals(Sets.newHashSet(3L, 4L), siteIdsRestrictionForCompanies);
  }

  private static Company createCompany(CompanyType type) {
    Company company = new Company(RandomStringUtils.randomAlphanumeric(32), type);
    company.setPid(randomLong());
    company.setStatus(Status.ACTIVE);
    return company;
  }

  private static long randomLong() {
    return Math.abs(new Random().nextLong());
  }
}
