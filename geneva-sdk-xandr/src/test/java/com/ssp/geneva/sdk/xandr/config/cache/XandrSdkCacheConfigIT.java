package com.ssp.geneva.sdk.xandr.config.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableCaching
@ContextConfiguration(classes = TestCacheConfig.class)
class XandrSdkCacheConfigIT {

  private static final String CACHE_NAME = "xandrAuthTokenCache";
  private static final String CACHE_KEY = "authToken";
  private static final String AUTH_TOKEN = "fh434hgd3g4fdh343";

  @Autowired private CacheManager xandrAuthTokenCacheManager;

  @Test
  void shouldCorrectlyCacheResourceFileContents() {
    Cache xandrAuthTokenCache = xandrAuthTokenCacheManager.getCache(CACHE_NAME);
    assertNotNull(xandrAuthTokenCache);
    xandrAuthTokenCache.put(CACHE_KEY, AUTH_TOKEN);
    assertNotNull(xandrAuthTokenCache.get(CACHE_KEY, String.class));
    assertEquals(AUTH_TOKEN, xandrAuthTokenCache.get(CACHE_KEY, String.class));
  }
}
