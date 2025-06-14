package com.nexage.app.config.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GenevaServerCacheConfig.class})
class GenevaServerCacheConfigIT {

  @Autowired private CacheManager allowedMdmIdsBySellerSeatCacheManager;

  @Test
  void shouldCreateCacheManagerBeans() {
    assertNotNull(allowedMdmIdsBySellerSeatCacheManager);
  }
}
