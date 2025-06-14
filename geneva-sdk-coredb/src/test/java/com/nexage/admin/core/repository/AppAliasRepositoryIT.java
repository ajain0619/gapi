package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.AppAlias;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(scripts = "/data/repository/app-alias.sql", config = @SqlConfig(encoding = "utf-8"))
class AppAliasRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private AppAliasRepository appAliasRepository;

  @Test
  void shouldFindByAppAliasValidAppAlias() {
    String appAlias = "Samsung TV Plus";

    AppAlias result = appAliasRepository.findByAppAlias(appAlias);
    assertEquals(appAlias, result.getAppAlias());
  }

  @Test
  void shouldNotFindByAppAliasWhenAppAliasDoesNotExist() {
    String appAlias = "this app alias doesn't exist";

    AppAlias result = appAliasRepository.findByAppAlias(appAlias);
    assertNull(result);
  }
}
