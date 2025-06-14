package com.nexage.app.metric;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codahale.metrics.MetricRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DoohScreenMetricsTest {

  private MetricRegistry metricRegistry;
  private DoohScreenMetrics doohScreenMetrics;

  @BeforeEach
  public void setUp() {
    metricRegistry = new MetricRegistry();
    doohScreenMetrics = new DoohScreenMetrics(metricRegistry);
  }

  @Test
  void shouldRecordCreatedScreenCountBySeller() {
    var seller1234 = "dooh.screen.management.created.histogram[seller_pid:1234]";
    var seller4321 = "dooh.screen.management.created.histogram[seller_pid:4321]";
    doohScreenMetrics.recordCreatedScreenCount(100, 1234L);
    doohScreenMetrics.recordCreatedScreenCount(400, 1234L);
    doohScreenMetrics.recordCreatedScreenCount(300, 4321L);
    doohScreenMetrics.recordCreatedScreenCount(200, 1234L);

    assertEquals(3, metricRegistry.histogram(seller1234).getCount());
    assertEquals(1, metricRegistry.histogram(seller4321).getCount());
    assertEquals(400, metricRegistry.histogram(seller1234).getSnapshot().getMax());
    assertEquals(300, metricRegistry.histogram(seller4321).getSnapshot().getMax());
  }

  @Test
  void shouldRecordFileUploadSuccessCountBySeller() {
    var seller1234 = "dooh.screen.management.file.success.count[seller_pid:1234]";
    var seller4321 = "dooh.screen.management.file.success.count[seller_pid:4321]";
    doohScreenMetrics.incrementFileUploadSuccess(1234L);
    doohScreenMetrics.incrementFileUploadSuccess(1234L);
    doohScreenMetrics.incrementFileUploadSuccess(4321L);

    assertEquals(2L, metricRegistry.counter(seller1234).getCount());
    assertEquals(1L, metricRegistry.counter(seller4321).getCount());
  }

  @Test
  void shouldRecordFileUploadErrorCountBySeller() {
    var seller1234 = "dooh.screen.management.file.error.count[seller_pid:1234]";
    var seller4321 = "dooh.screen.management.file.error.count[seller_pid:4321]";
    doohScreenMetrics.incrementFileUploadError(4321L);
    doohScreenMetrics.incrementFileUploadError(1234L);
    doohScreenMetrics.incrementFileUploadError(4321L);

    assertEquals(1L, metricRegistry.counter(seller1234).getCount());
    assertEquals(2L, metricRegistry.counter(seller4321).getCount());
  }
}
