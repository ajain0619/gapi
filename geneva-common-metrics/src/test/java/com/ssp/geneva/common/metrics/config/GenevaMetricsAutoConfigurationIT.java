package com.ssp.geneva.common.metrics.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.codahale.metrics.MetricRegistry;
import com.ssp.geneva.common.metrics.config.GenevaMetricsAutoConfigurationIT.TestApplicationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {TestApplicationProperties.class, GenevaMetricsAutoConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("metrics")
class GenevaMetricsAutoConfigurationIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldRegisterExpectedBeans() {
    var mbeanServer = context.getBean("mbeanServer");
    assertNotNull(mbeanServer);
    var mbeanExporter = context.getBean("mbeanExporter");
    assertNotNull(mbeanExporter);
    var metricRegistry = context.getBean("metricRegistry");
    assertNotNull(metricRegistry);
    var jmxReporter = context.getBean("jmxReporter");
    assertNotNull(jmxReporter);
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean(value = "metricRegistry")
    public MetricRegistry metricRegistry() {
      return new MetricRegistry();
    }
  }
}
