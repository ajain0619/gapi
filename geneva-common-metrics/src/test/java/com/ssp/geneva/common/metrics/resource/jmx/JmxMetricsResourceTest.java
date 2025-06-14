package com.ssp.geneva.common.metrics.resource.jmx;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codahale.metrics.jmx.JmxReporter;
import org.junit.jupiter.api.Test;

class JmxMetricsResourceTest {

  @Test
  void testShouldStopResource() {
    JmxReporter jmxReporter = mock(JmxReporter.class);
    JmxMetricsResource jmxMetricsResource = new JmxMetricsResource(jmxReporter);
    jmxMetricsResource.stop();
    verify(jmxReporter, times(1)).stop();
    verify(jmxReporter, never()).start();
  }

  @Test
  void testShouldStartResource() {
    JmxReporter jmxReporter = mock(JmxReporter.class);
    JmxMetricsResource jmxMetricsResource = new JmxMetricsResource(jmxReporter);
    jmxMetricsResource.start();
    verify(jmxReporter, times(1)).start();
    verify(jmxReporter, never()).stop();
  }
}
