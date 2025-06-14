package com.ssp.geneva.sdk.xandr.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestCacheConfig {

  @Bean("xandrAuthTokenCacheManager")
  public CacheManager getXandrAuthTokenCacheManager() {
    return new ConcurrentMapCacheManager("xandrAuthTokenCache");
  }
}
