package com.nexage.app.config;

import com.codahale.metrics.MetricRegistry;
import com.ssp.geneva.common.metrics.config.GenevaMetricsAutoConfiguration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.context.support.ServletContextAttributeExporter;

/**
 * This is the entrypoint for Geneva Server Metrics configuration as long as we have a mixed Xml and
 * Java configuration mechanism. Xml goes first, as long as there is not a directive before.
 *
 * <p>{@link AutoConfigureAfter annotation} does not provide the expected results until it is used
 * on a full-spring boot app. This is placed here as a preparation for that migration.
 */
@Configuration
@ImportResource({"classpath*:application-context-metrics.xml"})
@Import(GenevaMetricsAutoConfiguration.class)
@AutoConfigureAfter(GenevaMetricsAutoConfiguration.class)
public class GenevaServerMetricsServletConfig {

  @Bean("servletContextAttributeExporter")
  @ConditionalOnBean(name = "metricRegistry")
  @ConditionalOnClass({MetricRegistry.class})
  public ServletContextAttributeExporter servletContextAttributeExporter(
      @Autowired MetricRegistry metricRegistry) {
    ServletContextAttributeExporter exporter = new ServletContextAttributeExporter();
    exporter.setAttributes(
        Map.of(
            "com.codahale.metrics.servlets.MetricsServlet.registry",
            metricRegistry,
            "com.codahale.metrics.servlet.InstrumentedFilter.registry",
            metricRegistry));
    return exporter;
  }
}
