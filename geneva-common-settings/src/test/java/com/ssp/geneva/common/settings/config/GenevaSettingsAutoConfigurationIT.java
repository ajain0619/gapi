package com.ssp.geneva.common.settings.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.model.GlobalConfig;
import com.nexage.admin.core.repository.GlobalConfigRepository;
import com.ssp.geneva.common.settings.config.GenevaSettingsAutoConfigurationIT.TestApplicationProperties;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {TestApplicationProperties.class, GenevaSettingsAutoConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
class GenevaSettingsAutoConfigurationIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldRegisterExpectedBeans() {
    assertNotNull(context.getBean("globalConfigService"));
    assertNotNull(context.getBean("sysConfigUtil"));
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean("globalConfigRepository")
    public GlobalConfigRepository globalConfigRepository() {
      GlobalConfigRepository globalConfigRepository = mock(GlobalConfigRepository.class);
      GlobalConfig globalConfig = mock(GlobalConfig.class);
      when(globalConfig.getLongValue()).thenReturn(10000L);
      when(globalConfig.getBooleanValue()).thenReturn(true);
      when(globalConfigRepository.findByProperty((GlobalConfigProperty.GENEVA_ERROR_TRACE_ENABLED)))
          .thenReturn(Optional.of(globalConfig));
      when(globalConfigRepository.findByProperty(
              (GlobalConfigProperty.SSO_B2B_TOKEN_REFRESH_INTERVAL)))
          .thenReturn(Optional.of(globalConfig));
      return globalConfigRepository;
    }
  }
}
