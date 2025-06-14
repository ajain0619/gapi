package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.AdserverConfiguration;
import com.nexage.admin.core.model.AdserverConfiguration.AdserverConfigurationProperty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/adserver-configuration-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class AdserverConfigurationRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired protected AdserverConfigurationRepository adserverConfigurationRepository;

  @Test
  void findByProperty() {
    AdserverConfiguration adserverConfiguration;

    // get a property that exists
    adserverConfiguration =
        adserverConfigurationRepository.findByProperty(
            AdserverConfigurationProperty.CREATIVE_HOST_PROPERTIES.getPropertyName());
    assertNotNull(adserverConfiguration);

    // get a property that DOESN'T exists
    adserverConfiguration =
        adserverConfigurationRepository.findByProperty(
            AdserverConfigurationProperty.ADSERVER_HOST_PROPERTIES.getPropertyName());
    assertNull(adserverConfiguration);
  }
}
