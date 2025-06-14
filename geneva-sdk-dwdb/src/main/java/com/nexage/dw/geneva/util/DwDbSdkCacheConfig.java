package com.nexage.dw.geneva.util;

import com.ssp.geneva.common.cache.GenevaCacheAutoConfiguration;
import java.util.Collections;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GenevaCacheAutoConfiguration.class)
@AutoConfigureAfter(GenevaCacheAutoConfiguration.class)
@Log4j2
public class DwDbSdkCacheConfig {

  public DwDbSdkCacheConfig() {
    log.info("geneva.auto-config:DwCacheConfig");
  }

  @Bean("dwCacheManager")
  public CacheManager dwCacheManager() {
    log.info("Creating cache manager bean.");
    SimpleCacheManager dwCacheManager = new SimpleCacheManager();
    dwCacheManager.setCaches(
        Collections.singleton(new ConcurrentMapCache("screenedAdSqlResource")));
    return dwCacheManager;
  }
}
