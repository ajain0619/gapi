package com.ssp.geneva.common.metrics.config;

import com.codahale.metrics.jmx.JmxReporter;
import com.ssp.geneva.common.metrics.resource.datadog.DatadogMetricsResource;
import com.ssp.geneva.common.metrics.resource.jmx.JmxMetricsResource;
import lombok.extern.log4j.Log4j2;
import org.coursera.metrics.datadog.DatadogReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Log4j2
@Configuration
@Profile("metrics")
public class MetricsReporterConfig {

  @Bean("jmxMetricsResource")
  @ConditionalOnClass({JmxReporter.class})
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "geneva.metrics.jmx.enabled", havingValue = "true")
  public JmxMetricsResource jmxMetricsResource(@Autowired JmxReporter jmxReporter) {
    log.debug("Creating bean for class {}", JmxMetricsResource.class);
    return new JmxMetricsResource(jmxReporter);
  }

  @Bean("datadogMetricsResource")
  @ConditionalOnClass({DatadogReporter.class})
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "geneva.metrics.datadog.enabled", havingValue = "true")
  public DatadogMetricsResource datadogMetricsResource(@Autowired DatadogReporter datadogReporter) {
    log.debug("Creating bean for class {}", DatadogMetricsResource.class);
    return new DatadogMetricsResource(datadogReporter);
  }
}
