package com.ssp.geneva.common.metrics.config.jmx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.codahale.metrics.MetricRegistry;
import com.ssp.geneva.common.metrics.config.jmx.JmxReporterConfigIT.TestApplicationProperties;
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
@ContextConfiguration(classes = {TestApplicationProperties.class, JmxReporterConfig.class})
@TestPropertySource(properties = {"geneva.metrics.jmx.enabled=true"})
@ActiveProfiles("metrics")
class JmxReporterConfigIT {

  @Autowired private ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    var jmxReporterConfig = (JmxReporterConfig) context.getBean("jmxReporterConfig");
    assertNotNull(jmxReporterConfig);
    var metricsDomain = ReflectionTestUtils.getField(jmxReporterConfig, "metricsDomain");
    assertNotNull(metricsDomain);
    assertEquals("ssp.geneva.api", metricsDomain);
  }

  @Test
  void shouldRegisterExpectedBeans() {
    var jmxReporter = context.getBean("jmxReporter");
    assertNotNull(jmxReporter);
  }

  @Configuration
  public static class TestApplicationProperties {

    @Bean(value = "metricRegistry")
    public MetricRegistry metricRegistry() {
      return new MetricRegistry();
    }
  }
}
