package com.ssp.geneva.common.metrics.config.datadog;

import com.codahale.metrics.MetricRegistry;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.coursera.metrics.datadog.DatadogReporter;
import org.coursera.metrics.datadog.transport.UdpTransport;
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
public class DatadogReporterConfig {

  @Value("${geneva.metrics.datadog.period:600}")
  private Long datadogPeriod;

  @Bean("datadogReporter")
  @ConditionalOnMissingBean
  @ConditionalOnClass(MetricRegistry.class)
  @ConditionalOnProperty(name = "geneva.metrics.datadog.enabled", havingValue = "true")
  public DatadogReporter datadogReporter(@Autowired MetricRegistry metricRegistry) {
    log.info("Creating bean for class {}", this.getClass());
    return createReporter(metricRegistry);
  }

  private DatadogReporter createReporter(MetricRegistry registry) {

    EnumSet<DatadogReporter.Expansion> expansions = DatadogReporter.Expansion.ALL;
    DatadogReporter reporter =
        DatadogReporter.forRegistry(registry)
            .withTransport(new UdpTransport.Builder().build())
            .withExpansions(expansions)
            .build();
    reporter.start(datadogPeriod, TimeUnit.SECONDS);

    log.info("Datadog reporter={}", reporter);

    return reporter;
  }
}
