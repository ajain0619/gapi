package com.ssp.geneva.sdk.xandr.config.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class XandrSdkCacheConfigTest {
  private XandrSdkCacheConfig xandrSdkCacheConfig = new XandrSdkCacheConfig();

  @Test
  void shouldVerifyCacheManagerIsCreated() {
    CacheManager cacheManager = xandrSdkCacheConfig.getXandrAuthTokenCacheManager();
    assertNotNull(cacheManager);
  }
}
