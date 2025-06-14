package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
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
class DealSiteRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final Long DEAL_PID = 1L;

  @Autowired private DealSiteRepository dealSiteRepository;

  @Test
  void findSitesByDealPid() {
    var out = dealSiteRepository.findByDealPid(DEAL_PID);
    assertEquals(2, out.size());
  }

  @Test
  void deleteSitesByDealPid() {
    dealSiteRepository.deleteByDealPid(DEAL_PID);
    var out = dealSiteRepository.findByDealPid(DEAL_PID);
    assertEquals(0, out.size());
  }
}
