package com.ssp.geneva.sdk.xandr.config.cache;

import com.ssp.geneva.common.cache.GenevaCacheAutoConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Log4j2
@Configuration
@Import(GenevaCacheAutoConfiguration.class)
@AutoConfigureAfter(GenevaCacheAutoConfiguration.class)
public class XandrSdkCacheConfig extends CachingConfigurerSupport {

  public XandrSdkCacheConfig() {
    log.info("geneva.auto-config:GenevaCacheAutoConfiguration");
  }

  /**
   * Creates a {@link CacheManager} for the xandrAuthToken.
   *
   * @return the created {@link CacheManager}
   */
  @Bean("xandrAuthTokenCacheManager")
  public CacheManager getXandrAuthTokenCacheManager() {
    return new ConcurrentMapCacheManager("xandrAuthTokenCache");
  }
}
