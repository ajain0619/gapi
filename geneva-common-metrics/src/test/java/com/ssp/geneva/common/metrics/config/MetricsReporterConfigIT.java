package com.ssp.geneva.common.metrics.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.codahale.metrics.MetricRegistry;
import com.ssp.geneva.common.metrics.config.MetricsReporterConfigIT.TestApplicationProperties;
import com.ssp.geneva.common.metrics.config.datadog.DatadogReporterConfig;
import com.ssp.geneva.common.metrics.config.jmx.JmxReporterConfig;
import com.ssp.geneva.common.metrics.resource.datadog.DatadogMetricsResource;
import com.ssp.geneva.common.metrics.resource.jmx.JmxMetricsResource;
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
    classes = {
      TestApplicationProperties.class,
      JmxReporterConfig.class,
      DatadogReporterConfig.class,
      MetricsReporterConfig.class
    })
@TestPropertySource(
    properties = {"geneva.metrics.datadog.enabled=true", "geneva.metrics.jmx.enabled=true"})
@ActiveProfiles("metrics")
class MetricsReporterConfigIT {

  @Autowired private ApplicationContext context;

  @Test
  void shouldRegisterExpectedBeans() {
    var jmxMetricsResource = (JmxMetricsResource) context.getBean("jmxMetricsResource");
    assertNotNull(jmxMetricsResource);
    var datadogMetricsResource = (DatadogMetricsResource) context.getBean("datadogMetricsResource");
    assertNotNull(datadogMetricsResource);
  }

  @Configuration
  public static class TestApplicationProperties {

    @Bean(value = "metricRegistry")
    public MetricRegistry metricRegistry() {
      return new MetricRegistry();
    }
  }
}
