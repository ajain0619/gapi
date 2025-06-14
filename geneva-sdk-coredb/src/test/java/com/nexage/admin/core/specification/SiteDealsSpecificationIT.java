package com.nexage.admin.core.specification;

import static com.nexage.admin.core.specification.SiteDealsSpecification.withDealPidAndSellerIds;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.repository.DealSiteRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/deals-common.sql", "/data/repository/deal-site-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class SiteDealsSpecificationIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private DealSiteRepository dealSiteRepository;

  @Test
  void shouldCorrectlySelectDealsWithDealPidAndSellerIds() {
    var dealsCount = dealSiteRepository.count(withDealPidAndSellerIds(1L, List.of(1L)));
    Assertions.assertEquals(2, dealsCount);
  }
}
