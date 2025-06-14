package com.ssp.geneva.common.settings.config;

import com.nexage.admin.core.config.CoreDbSdkConfig;
import com.nexage.admin.core.repository.GlobalConfigRepository;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import com.ssp.geneva.common.settings.service.impl.GlobalConfigServiceImpl;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Log4j2
@Configuration
@AutoConfigureAfter({CoreDbSdkConfig.class})
@ComponentScan(basePackages = {"com.ssp.geneva.common.settings"})
public class GenevaSettingsAutoConfiguration {

  public GenevaSettingsAutoConfiguration() {
    log.info("geneva.auto-config:GenevaSettingsConfig");
  }

  @ConditionalOnClass(GlobalConfigRepository.class)
  @Bean("globalConfigService")
  public GlobalConfigService globalConfigService(
      @Autowired GlobalConfigRepository globalConfigRepository) {
    return new GlobalConfigServiceImpl(globalConfigRepository);
  }

  @Bean("sysConfigUtil")
  public SysConfigUtil sysConfigUtil(
      @Autowired GlobalConfigService globalConfigService, @Autowired Environment environment) {
    return new SysConfigUtil(globalConfigService, environment);
  }
}
