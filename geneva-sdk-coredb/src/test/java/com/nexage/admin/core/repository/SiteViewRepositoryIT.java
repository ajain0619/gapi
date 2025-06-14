package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.SiteView;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/site-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class SiteViewRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final Long SELLER_PID = 11L;

  private static final Long SELLER_SEAT_PID = 12345L;

  private static final Map<Long, String> ALL_SITES = Map.of(1L, "url", 2L, "ssfl", 3L, "ssfl2");

  @Autowired private SiteViewRepository siteViewRepository;

  @Test
  void shouldFindAllSellerSites() {
    // given
    Pageable pageable = PageRequest.of(0, 5);

    // when
    var out = siteViewRepository.findAllSellerSites(SELLER_PID, pageable);

    // then
    assertAllSitesArePresent(out, "Nexage Inc");
  }

  @Test
  void shouldFindSiteByName() {
    // given
    Pageable pageable = PageRequest.of(0, 5);

    // when
    var out = siteViewRepository.searchSellerSitesByName(SELLER_PID, "name", pageable);

    // then
    assertAllSitesArePresent(out, "Nexage Inc");
  }

  @Test
  @SqlGroup({
    @Sql(
        scripts = "/data/repository/seller-seat-repository.sql",
        config = @SqlConfig(encoding = "utf-8")),
    @Sql(statements = "UPDATE site SET company_pid = 300 WHERE pid IN (1, 2, 3)")
  })
  void shouldFindAllSellerSitesForSellerSeat() {
    // given
    Pageable pageable = PageRequest.of(0, 5);

    // when
    var out = siteViewRepository.findAllSellerSitesForSellerSeat(SELLER_SEAT_PID, pageable);

    // then
    assertAllSitesArePresent(out, "Test 1");
  }

  @Test
  @SqlGroup({
    @Sql(
        scripts = "/data/repository/seller-seat-repository.sql",
        config = @SqlConfig(encoding = "utf-8")),
    @Sql(statements = "UPDATE site SET company_pid = 300 WHERE pid IN (1, 2, 3)")
  })
  void shouldFindSiteByNameForSellerSeat() {
    // given
    Pageable pageable = PageRequest.of(0, 5);

    // when
    var out =
        siteViewRepository.searchSellerSitesByNameForSellerSeat(SELLER_SEAT_PID, "name", pageable);

    // then
    assertAllSitesArePresent(out, "Test 1");
  }

  private void assertAllSitesArePresent(Page<SiteView> out, String companyName) {
    assertEquals(ALL_SITES.size(), out.getTotalElements());
    ALL_SITES.forEach(
        (pid, url) -> {
          assertTrue(
              out.stream()
                  .anyMatch(
                      siteView ->
                          siteView.getPid().equals(pid)
                              && siteView.getUrl().equals(url)
                              && siteView.getCompany().getName().equals(companyName)));
        });
  }
}
