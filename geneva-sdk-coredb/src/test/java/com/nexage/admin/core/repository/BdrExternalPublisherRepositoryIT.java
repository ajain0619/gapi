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
    scripts = "/data/repository/bdr-external-site-publisher-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BdrExternalPublisherRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired BdrExternalPublisherRepository externalPublisherRepository;

  @Test
  void shouldFindAllExternalPublishers() {
    assertEquals(5, externalPublisherRepository.findAll().size());
  }
}
