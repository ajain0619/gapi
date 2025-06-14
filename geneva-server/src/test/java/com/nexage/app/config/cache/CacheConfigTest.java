package com.nexage.app.config.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;

class CacheConfigTest {
  private GenevaServerCacheConfig allowedSourceIdsBySellerSeatsCacheConfig =
      new GenevaServerCacheConfig();

  @Test
  void testGetAllowedSourceIdsBySellerSeatsCacheManagerIsCreatedCorrectly() {
    CacheManager cacheManager =
        allowedSourceIdsBySellerSeatsCacheConfig.getAllowedSourceIdsBySellerSeatsCacheManager();
    assertNotNull(cacheManager);
  }
}
