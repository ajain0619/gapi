package com.ssp.geneva.common.metrics.config.jmx;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Log4j2
@Configuration
@Profile("metrics")
public class JmxReporterConfig {

  @Value("${geneva.metrics.jmx.domain:ssp.geneva.api}")
  private String metricsDomain;

  @Bean("jmxReporter")
  @ConditionalOnMissingBean
  @ConditionalOnClass(MetricRegistry.class)
  @ConditionalOnProperty(name = "geneva.metrics.jmx.enabled", havingValue = "true")
  public JmxReporter jmxReporter(@Autowired MetricRegistry metricRegistry) {
    log.info("Creating bean for class {}", this.getClass());
    return JmxReporter.forRegistry(metricRegistry).inDomain(metricsDomain).build();
  }
}
