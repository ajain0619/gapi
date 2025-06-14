package com.ssp.geneva.common.cache;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

@Log4j2
@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "geneva.common.cache", name = "enabled", havingValue = "true")
public class GenevaCacheAutoConfiguration {

  public GenevaCacheAutoConfiguration() {
    log.info("geneva.auto-config:GenevaCacheAutoConfiguration");
  }

  @Bean
  public EhCacheManagerFactoryBean cacheFactoryBean() {
    var cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
    cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
    cacheManagerFactoryBean.setShared(true);
    return cacheManagerFactoryBean;
  }

  @Primary
  @Bean("ehCacheCacheManager")
  public CacheManager ehCacheCacheManager(
      @Autowired EhCacheManagerFactoryBean cacheManagerFactoryBean) {
    return new EhCacheCacheManager(cacheManagerFactoryBean.getObject());
  }
}
