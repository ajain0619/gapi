package com.nexage.app.config.health;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.nexage.app.config.health.GenevaServerHealthCheckConfigIT.TestApplicationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {TestApplicationProperties.class, GenevaServerHealthCheckConfig.class})
class GenevaServerHealthCheckConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldLoadContext() {
    var coreDbHealthCheck = context.getBean("coreDbHealthCheck");
    assertNotNull(coreDbHealthCheck);

    var dwDbHealthCheck = context.getBean("dwDbHealthCheck");
    assertNotNull(dwDbHealthCheck);

    var healthCheckService = context.getBean("healthCheckService");
    assertNotNull(healthCheckService);
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean(value = "coreJdbcTemplate")
    public JdbcTemplate coreJdbcTemplate() {
      return mock(JdbcTemplate.class);
    }

    @Bean(value = "dwJdbcTemplate")
    public JdbcTemplate dwJdbcTemplate() {
      return mock(JdbcTemplate.class);
    }
  }
}
