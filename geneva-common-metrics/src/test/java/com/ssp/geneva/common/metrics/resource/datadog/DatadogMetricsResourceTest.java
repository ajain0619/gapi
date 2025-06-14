package com.ssp.geneva.common.metrics.resource.datadog;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.coursera.metrics.datadog.DatadogReporter;
import org.junit.jupiter.api.Test;

class DatadogMetricsResourceTest {

  @Test
  void testShouldStopResource() {
    DatadogReporter datadogReporter = mock(DatadogReporter.class);
    DatadogMetricsResource datadogMetricsResource = new DatadogMetricsResource(datadogReporter);
    datadogMetricsResource.stop();
    verify(datadogReporter, times(1)).stop();
  }

  @Test
  void testShouldStartResource() {
    DatadogReporter datadogReporter = mock(DatadogReporter.class);
    DatadogMetricsResource datadogMetricsResource = new DatadogMetricsResource(datadogReporter);
    datadogMetricsResource.start();
    verifyNoInteractions(datadogReporter);
  }
}
