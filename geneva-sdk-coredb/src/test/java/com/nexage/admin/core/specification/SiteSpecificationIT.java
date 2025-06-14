package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Sets;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.SiteRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(scripts = "/data/repository/site-repository.sql", config = @SqlConfig(encoding = "utf-8"))
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class SiteSpecificationIT extends CoreDbSdkIntegrationTestBase {

  private static final long PID = 11L;
  private static final String SITE_NAME = "Site with name-1";

  private static final Long SELLER_SEAT_PID = 12345L;

  @Autowired private SiteRepository siteRepository;

  @Test
  void shouldFindSpecificationWithGivenCompanyPid() {
    // given
    Specification<Site> spec = SiteSpecification.withSellerId(PID);

    // when
    long countSpec = siteRepository.count(spec);

    // then
    assertEquals(3, countSpec);
  }

  @Test
  void shouldFindSpecificationWithGivenName() {
    // given
    Specification<Site> spec = SiteSpecification.withNameLike(SITE_NAME);

    // when
    long countSpec = siteRepository.count(spec);

    // then
    assertEquals(1, countSpec);
  }

  @Test
  void shouldFindSpecificationWithStatus() {
    // given
    Specification<Site> spec = SiteSpecification.withStatus(Status.ACTIVE);

    // when
    long countSpec = siteRepository.count(spec);

    // then
    assertEquals(2, countSpec);
  }

  @Test
  void shouldFindSpecificationWithStatusFromList() {
    // given
    Specification<Site> spec = SiteSpecification.withStatus(List.of("ACTIVE", "DELETED"));

    // when
    long countSpec = siteRepository.count(spec);

    // then
    assertEquals(3, countSpec);
  }

  @Test
  void shouldFindSpecificationWithSearchSitesAndPositions() {
    // given
    Specification<Site> spec = SiteSpecification.searchSitesAndPositions("Site with name-3");

    // when
    long countSpec = siteRepository.count(spec);

    // then
    assertEquals(1, countSpec);
  }

  @Test
  void shouldFindSpecificationWithIds() {
    // given
    Specification<Site> spec = SiteSpecification.withIds("companyPid", Sets.newHashSet(11L));

    // when
    long countSpec = siteRepository.count(spec);

    // then
    assertEquals(3, countSpec);
  }

  @Test
  void shouldFindSpecificationWithSiteTypes() {
    // given
    List<String> siteTypes = List.of("MOBILE_WEB", "WEBSITE");
    var resultListWithTypeAndPlatform = SiteSpecification.typeAndPlatformFromSiteTypes(siteTypes);
    var resultWithSiteTypes = SiteSpecification.withSiteTypes(resultListWithTypeAndPlatform);

    // when
    long countSpec = siteRepository.count(resultWithSiteTypes);

    // then
    assertEquals(3, countSpec);
  }

  @Test
  @SqlGroup({
    @Sql(
        scripts = "/data/repository/seller-seat-repository.sql",
        config = @SqlConfig(encoding = "utf-8")),
    @Sql(statements = "UPDATE site SET company_pid = 300 WHERE pid = 1")
  })
  void shouldFindLimitedSitesBasedOnSellerSeatPid() {
    // given
    Specification<Site> spec = SiteSpecification.withSellerSeatPid(SELLER_SEAT_PID);

    // when
    long result = siteRepository.count(spec);

    // then
    assertEquals(1, result);
  }
}
