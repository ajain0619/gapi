package com.ssp.geneva.common.error.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.ssp.geneva.common.base.settings.SystemConfigurable;
import com.ssp.geneva.common.error.config.GenevaErrorAutoConfigurationIT.TestApplicationProperties;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {TestApplicationProperties.class, GenevaErrorAutoConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
class GenevaErrorAutoConfigurationIT {

  @Autowired ConfigurableApplicationContext context;

  @Test
  void shouldRegisterExpectedBeans() {
    Properties properties = (Properties) context.getBean("exceptionLoggerProperties");
    assertNotNull(context.getBean("exceptionLoggerProperties"));
    assertTrue(
        properties.containsKey("com.ssp.geneva.common.error.exception.GenevaValidationException"));
    assertTrue(
        Boolean.parseBoolean(
            properties.getProperty(
                "com.ssp.geneva.common.error.exception.GenevaValidationException")));
    assertTrue(
        properties.containsKey("com.ssp.geneva.common.error.exception.GenevaAppRuntimeException"));
    assertFalse(
        Boolean.parseBoolean(
            properties.getProperty(
                "com.ssp.geneva.common.error.exception.GenevaAppRuntimeException")));
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean("sysConfigUtil")
    public SystemConfigurable systemConfigurable() {
      return mock(SystemConfigurable.class);
    }
  }
}
