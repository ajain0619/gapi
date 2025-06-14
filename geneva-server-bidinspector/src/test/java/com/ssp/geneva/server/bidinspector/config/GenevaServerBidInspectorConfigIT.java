package com.ssp.geneva.server.bidinspector.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
      GenevaServerBidInspectorConfigIT.TestApplicationProperties.class,
      GenevaServerBidInspectorConfig.class
    })
@TestPropertySource("classpath:application-test.properties")
class GenevaServerBidInspectorConfigIT {
  @Autowired ApplicationContext context;

  @Test
  void shouldRegisterExpectedBeans() {
    assertNotNull(context.getBean("bidDao"));
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean("coreServicesJdbcTemplate")
    public JdbcTemplate coreServicesJdbcTemplate() {
      return mock(JdbcTemplate.class);
    }

    @Bean("dwJdbcTemplate")
    public JdbcTemplate dwJdbcTemplate() {
      return mock(JdbcTemplate.class);
    }
  }
}
