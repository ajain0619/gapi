package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.dto.SiteSummaryDTO;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Site;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/site-repository.sql", config = @SqlConfig(encoding = "utf-8"))
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class SiteRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final Long SITE1_PID = 1L;
  private static final Long SITE2_DELETED_PID = 2L;
  private static final Long SITE3_RESTRICTED_PID = 3L;
  private static final Long COMPANY_PID = 11L;
  private static final Long USER_PID = 111L;

  private static final Long SELLER_SEAT_PID = 12345L;
  private static final Long SELLER_SEAT_COMPANY_PID = 300L;

  @Autowired SiteRepository siteRepository;

  @Test
  void shouldCountSiteAssociationsByCompanyPidAndHbPartnerPids() {
    assertEquals(
        1,
        siteRepository
            .countSiteAssociationsByCompanyPidAndHbPartnerPids(COMPANY_PID, List.of(1L))
            .intValue(),
        "invalid count of companies");
    assertEquals(
        0,
        siteRepository
            .countSiteAssociationsByCompanyPidAndHbPartnerPids(COMPANY_PID, List.of(2L))
            .intValue(),
        "invalid count of companies");
  }

  @Test
  void shouldFindBySitePidIn() {
    // when
    var out = siteRepository.findBySitePidIn(List.of(SITE1_PID));

    // then
    assertEquals(1, out.size());
    var siteView = out.get(0);
    assertEquals(SITE1_PID, siteView.getPid());
    assertEquals("Site with name-1", siteView.getName());
    assertEquals(COMPANY_PID, siteView.getCompany().getPid());
  }

  @Test
  void shouldFindLimitedSiteByCompanyPid() {
    // when
    var out = siteRepository.findLimitedSiteByCompanyPid(COMPANY_PID, PageRequest.of(0, 10));

    // then
    assertEquals(2, out.getTotalElements());
    var siteView = out.getContent().get(0);
    assertEquals(SITE1_PID, siteView.getPid());
    assertEquals("Site with name-1", siteView.getName());
    assertEquals(COMPANY_PID, siteView.getCompanyPid());
    assertNull(siteView.getDescription());
    assertNull(siteView.getType());
    assertNull(siteView.getDomain());
  }

  @Test
  @SqlGroup({
    @Sql(
        scripts = "/data/repository/seller-seat-repository.sql",
        config = @SqlConfig(encoding = "utf-8")),
    @Sql(statements = "UPDATE site SET company_pid = 300 WHERE pid = 1")
  })
  void shouldFindLimitedSiteBySellerSeatPid() {
    // when
    var out = siteRepository.findLimitedSiteBySellerSeatPid(SELLER_SEAT_PID, PageRequest.of(0, 10));

    // then
    assertEquals(1, out.getTotalElements());
    Site siteView = out.getContent().get(0);
    assertEquals(SITE1_PID, siteView.getPid());
    assertEquals("Site with name-1", siteView.getName());
    assertEquals(SELLER_SEAT_COMPANY_PID, siteView.getCompanyPid());
    assertNull(siteView.getDescription());
    assertNull(siteView.getType());
    assertNull(siteView.getDomain());
  }

  @Test
  void shouldFindPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted() {
    // when
    var sites =
        siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
            USER_PID, Set.of(COMPANY_PID));

    // then
    assertEquals(Set.of(SITE1_PID), sites);
  }

  @Test
  void shouldFindPidsByCompanyPidsWithStatusNotDeleted() {
    // when
    var sites = siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(Set.of(COMPANY_PID));

    // then
    assertEquals(Set.of(SITE1_PID, SITE3_RESTRICTED_PID), sites);
  }

  @Test
  void shouldProperlyCheckIfSiteExistsByDcn() {
    assertTrue(siteRepository.existsByDcn("dcn1"));
    assertFalse(siteRepository.existsByDcn("non_existing_site_dcn"));
  }

  @Test
  void shouldProperlyCheckIfSiteExistsByCompanyPidAndStatusNot() {
    assertTrue(siteRepository.existsByCompanyPidAndStatusNot(COMPANY_PID, Status.ACTIVE));
    assertFalse(siteRepository.existsByCompanyPidAndStatusNot(2L, Status.INACTIVE));
  }

  @Test
  void shouldProperlyCheckIfSiteExistsByPidNotAndCompanyPidAndName() {
    assertTrue(
        siteRepository.existsByPidNotAndCompanyPidAndName(
            SITE2_DELETED_PID, COMPANY_PID, "Site with name-1"));
    assertFalse(
        siteRepository.existsByPidNotAndCompanyPidAndName(
            SITE2_DELETED_PID, 2L, "non_existing_name"));
  }

  @Test
  void shouldProperlyCheckIfSiteExistsByPidAndCompanyPidAndStatus() {
    assertTrue(
        siteRepository.existsByPidAndCompanyPidAndStatus(SITE1_PID, COMPANY_PID, Status.ACTIVE));
    assertFalse(
        siteRepository.existsByPidAndCompanyPidAndStatus(SITE2_DELETED_PID, 2L, Status.INACTIVE));
  }

  @Test
  void shouldFindSitePidsByCompanyPid() {
    // when
    List<Long> pids = siteRepository.findPidsByCompanyPid(COMPANY_PID);

    // then
    assertEquals(List.of(SITE1_PID, SITE2_DELETED_PID, SITE3_RESTRICTED_PID), pids);
  }

  @Test
  void shouldCountByCompanyPidAndStatusCorrectly() {
    // when
    long siteCount = siteRepository.countByCompanyPidAndStatusNot(COMPANY_PID, Status.DELETED);

    // then
    assertEquals(2L, siteCount);
  }

  @Test
  void shouldFindSummaryDtosByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted() {
    // when
    List<SiteSummaryDTO> summaries =
        siteRepository.findSummaryDtosByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
            USER_PID, Set.of(COMPANY_PID));

    // then
    assertEquals(1, summaries.size());
    assertTrue(summaries.contains(getExpectedSummary()));
  }

  @Test
  void shouldFindSummaryDtosByCompanyPidWithStatusNotDeleted() {
    // when
    List<SiteSummaryDTO> summaries =
        siteRepository.findSummaryDtosByCompanyPidWithStatusNotDeleted(COMPANY_PID);

    // then
    assertEquals(2, summaries.size());
    assertTrue(summaries.contains(getExpectedSummary()));
  }

  @Test
  void shouldFindSummaryDtosWithStatusNotDeleted() {
    // when
    List<SiteSummaryDTO> summaries = siteRepository.findSummaryDtosWithStatusNotDeleted();

    // then
    assertEquals(2, summaries.size());
    assertTrue(summaries.contains(getExpectedSummary()));
  }

  @Test
  void shouldSuccessfullyRunQueryWhenFindSiteWithInactiveTagProjectionsByPid() {
    assertTrue(siteRepository.findSiteWithInactiveTagProjectionsByPid(SITE1_PID).isEmpty());
  }

  @Test
  void shouldSuccessfullyRunQueryWhenFindAllSiteWithInactiveTagProjections() {
    assertTrue(siteRepository.findAllSiteWithInactiveTagProjections().isEmpty());
  }

  private SiteSummaryDTO getExpectedSummary() {
    return new SiteSummaryDTO(
        "4028811s1a242984011a74276dd12eee",
        SITE1_PID,
        "url",
        "Site with name-1",
        "global_alias_name1",
        Type.MOBILE_WEB,
        Platform.OTHER,
        Status.ACTIVE,
        true,
        COMPANY_PID,
        "Nexage Inc",
        "site1.com");
  }
}
