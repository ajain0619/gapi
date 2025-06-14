package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {
      "/data/repository/position-repository.sql",
      "/data/repository/deal-term-view-repository.sql"
    },
    config = @SqlConfig(encoding = "utf-8"))
class DealTermViewRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private DealTermViewRepository dealTermViewRepository;

  @Test
  void fetchTagDealtermsSuccess() {
    var sitePid = 1L;
    var tagPid = List.of(2L);
    var out =
        dealTermViewRepository.findBySitePidAndTagPidInOrderByEffectiveDateDesc(sitePid, tagPid);
    assertEquals(2, out.size());
  }
}
