package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.model.GlobalConfig;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/global-config-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class GlobalConfigRepositoryIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private GlobalConfigRepository globalConfigRepository;

  @Test
  void shouldCorrectlyRetrieveTestConfig() {
    assertEquals(
        List.of(1111L, 2222L, 3333L),
        globalConfigRepository
            .findByProperty(GlobalConfigProperty.DEAL_ZERO_COST_SELLER_ALLOW_LIST)
            .map(GlobalConfig::getLongListValue)
            .get());
  }
}
