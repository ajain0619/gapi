package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/phone-cast-configuration-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class PhoneCastConfigurationRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private PhoneCastConfigurationRepository phoneCastConfigurationRepository;

  @Test
  void shouldFetchEntryByConfigKeyIfItDoesExist() {
    // given
    String configKey = "geneva.pf.update.job.username";
    // when
    var configEntry = phoneCastConfigurationRepository.findByConfigKey(configKey);
    // then
    assertTrue(configEntry.isPresent());
    assertEquals("svc-geneva-api", configEntry.get().getConfigValue());
  }

  @Test
  void shouldNotFetchEntryByConfigKeyIfItDoesNotExist() {
    // given
    String configKey = "potato";
    // when
    var configEntry = phoneCastConfigurationRepository.findByConfigKey(configKey);
    // then
    assertTrue(configEntry.isEmpty());
  }
}
