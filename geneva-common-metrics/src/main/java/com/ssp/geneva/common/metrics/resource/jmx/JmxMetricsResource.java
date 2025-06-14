package com.ssp.geneva.common.metrics.resource.jmx;

import static java.util.Objects.nonNull;

import com.codahale.metrics.jmx.JmxReporter;
import com.ssp.geneva.common.metrics.resource.MetricsResource;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@Log4j2
@ManagedResource(
    objectName = "ssp.geneva.api:name=JmxMetricsResource",
    description = "Bean that handles exporting metrics collected in the metric registry")
@Profile("metrics")
public class JmxMetricsResource implements MetricsResource {

  private final JmxReporter jmxReporter;

  public JmxMetricsResource(JmxReporter jmxReporter) {
    this.jmxReporter = jmxReporter;
  }

  @ManagedOperation(description = "Start JMX Metrics Reporter")
  public void start() {
    jmxReporter.start();
    log.info("Metrics > JMX Metrics Reporter started.");
  }

  @ManagedOperation(description = "Stop JMX Metrics Reporter")
  public void stop() {
    if (nonNull(jmxReporter)) {
      log.info("Metrics > Stopping JMX Metrics Reporter.");
      jmxReporter.stop();
    }
  }

  @PostConstruct
  private void init() {
    start();
  }

  @PreDestroy
  private void finish() {
    stop();
  }
}
