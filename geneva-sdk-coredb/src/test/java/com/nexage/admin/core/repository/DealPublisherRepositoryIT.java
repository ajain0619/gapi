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
    scripts = {
      "/data/repository/deals-common.sql",
      "/data/repository/deal-publisher-repository.sql"
    },
    config = @SqlConfig(encoding = "utf-8"))
class DealPublisherRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final Long DEAL_PID = 1L;

  @Autowired private DealPublisherRepository dealPublisherRepository;

  @Test
  void shouldFindPublishersByDealPid() {
    var out = dealPublisherRepository.findByDealPid(DEAL_PID);
    assertEquals(2, out.size());
  }

  @Test
  void shouldDeletePublishersByDealPid() {
    dealPublisherRepository.deleteByDealPid(DEAL_PID);
    var out = dealPublisherRepository.findByDealPid(DEAL_PID);
    assertEquals(0, out.size());
  }
}
