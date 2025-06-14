package com.ssp.geneva.common.metrics.config.datadog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.codahale.metrics.MetricRegistry;
import com.ssp.geneva.common.metrics.config.datadog.DatadogReporterConfigIT.TestApplicationProperties;
import org.coursera.metrics.datadog.DatadogReporter;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestApplicationProperties.class, DatadogReporterConfig.class})
@TestPropertySource(properties = {"geneva.metrics.datadog.enabled=true"})
@ActiveProfiles("metrics")
class DatadogReporterConfigIT {

  @Autowired private ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    var datadogReporterConfig = (DatadogReporterConfig) context.getBean("datadogReporterConfig");
    assertNotNull(datadogReporterConfig);
    var datadogPeriod = ReflectionTestUtils.getField(datadogReporterConfig, "datadogPeriod");
    assertNotNull(datadogPeriod);
    assertEquals(600L, datadogPeriod);
  }

  @Test
  void shouldRegisterExpectedBeans() {
    var datadogReporter = (DatadogReporter) context.getBean("datadogReporter");
    assertNotNull(datadogReporter);
  }

  @Configuration
  public static class TestApplicationProperties {

    @Bean(value = "metricRegistry")
    public MetricRegistry metricRegistry() {
      return new MetricRegistry();
    }
  }
}
