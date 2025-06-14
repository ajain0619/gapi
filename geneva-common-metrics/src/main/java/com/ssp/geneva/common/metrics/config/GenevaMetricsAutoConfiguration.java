package com.ssp.geneva.common.metrics.config;

import com.ssp.geneva.common.base.annotation.Incubating;
import com.ssp.geneva.common.metrics.config.datadog.DatadogReporterConfig;
import com.ssp.geneva.common.metrics.config.jmx.JmxConfig;
import com.ssp.geneva.common.metrics.config.jmx.JmxReporterConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * This class is the pre-step before removing application context via Xml files for metrics.
 *
 * <p>There are two ways of loading the {@link com.codahale.metrics.MetricRegistry}: java & xml
 *
 * <p>- Java: @EnableMetrics
 *
 * <p>- Xml: application-context-metrics.xml
 *
 * <p>Once we can get rid of the xml version, we must add the annotation to this class.
 */
@Log4j2
@Configuration
@Import({
  MetricsReporterConfig.class,
  JmxConfig.class,
  JmxReporterConfig.class,
  DatadogReporterConfig.class
})
@Incubating(name = "https://jira.vzbuilders.com/browse/SSP-18735")
public class GenevaMetricsAutoConfiguration {

  public GenevaMetricsAutoConfiguration() {
    log.info("geneva.auto-config:GenevaMetricsAutoConfiguration");
  }
}
