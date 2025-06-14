package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codahale.metrics.MetricRegistry;
import com.nexage.app.config.GenevaServerMetricsServletConfigIT.TestApplicationProperties;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.support.ServletContextAttributeExporter;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {TestApplicationProperties.class, GenevaServerMetricsServletConfig.class})
class GenevaServerMetricsServletConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    var servletContextAttributeExporter =
        (ServletContextAttributeExporter) context.getBean("servletContextAttributeExporter");
    assertNotNull(servletContextAttributeExporter);

    var attributes = ReflectionTestUtils.getField(servletContextAttributeExporter, "attributes");
    assertNotNull(attributes);
    Map<String, Object> mappedAttributes = (Map<String, Object>) attributes;
    assertFalse(mappedAttributes.isEmpty());
    assertTrue(
        mappedAttributes.containsKey("com.codahale.metrics.servlets.MetricsServlet.registry"));
    assertTrue(
        mappedAttributes.containsKey("com.codahale.metrics.servlet.InstrumentedFilter.registry"));
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean(value = "metricRegistry")
    public MetricRegistry metricRegistry() {
      return new MetricRegistry();
    }
  }
}
