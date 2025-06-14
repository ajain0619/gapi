package com.ssp.geneva.common.metrics.resource.datadog;

import static java.util.Objects.nonNull;

import com.ssp.geneva.common.metrics.resource.MetricsResource;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.coursera.metrics.datadog.DatadogReporter;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@Log4j2
@ManagedResource(
    objectName = "ssp.geneva.api:name=DatadogMetricsResource",
    description = "Bean that handles exporting metrics collected in the metric registry")
@Profile("metrics")
public class DatadogMetricsResource implements MetricsResource {

  private final DatadogReporter datadogReporter;

  public DatadogMetricsResource(DatadogReporter datadogReporter) {
    this.datadogReporter = datadogReporter;
  }

  @ManagedOperation(description = "Start Datadog Metrics Reporter")
  public void start() {
    log.info("Metrics > Datadog Metrics Reporter started.");
  }

  @ManagedOperation(description = "Stop Datadog Metrics Reporter")
  public void stop() {
    if (nonNull(datadogReporter)) {
      log.info("Metrics > Stopping Datadog Metrics Reporter.");
      datadogReporter.stop();
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
