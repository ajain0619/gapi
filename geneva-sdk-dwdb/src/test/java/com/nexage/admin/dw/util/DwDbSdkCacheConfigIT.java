package com.nexage.admin.dw.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.dw.geneva.util.DwDbSdkCacheConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DwDbSdkCacheConfig.class})
class DwDbSdkCacheConfigIT {

  @Autowired CacheManager cacheManager;

  @Test
  void testCacheManagerBeanCreation() {
    assertNotNull(cacheManager);
  }
}
