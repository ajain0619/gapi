package com.ssp.geneva.common.security.config;

import com.ssp.geneva.common.security.filter.sso.SingleSignOnSessionFilter;
import com.ssp.geneva.common.settings.config.GenevaSettingsAutoConfiguration;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Configuration
@ConditionalOnProperty(
    prefix = "geneva.common.security",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@AutoConfigureAfter({GenevaSettingsAutoConfiguration.class})
@ComponentScan(basePackages = {"com.ssp.geneva.common.security"})
public class GenevaSecurityAutoConfiguration {

  public GenevaSecurityAutoConfiguration() {
    log.info("geneva.auto-config:GenevaSecurityAutoConfiguration");
  }

  @Bean("singleSignOnSessionFilter")
  public SingleSignOnSessionFilter singleSignOnSessionFilter(
      @Autowired OAuth2RestTemplate restTemplate,
      @Autowired RestTemplate simpleRestTemplate,
      @Autowired SysConfigUtil sysConfigUtil,
      @Autowired GenevaSecurityProperties genevaSecurityProperties,
      OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
    return new SingleSignOnSessionFilter(
        restTemplate,
        simpleRestTemplate,
        sysConfigUtil,
        genevaSecurityProperties,
        oAuth2AuthorizedClientRepository);
  }
}
