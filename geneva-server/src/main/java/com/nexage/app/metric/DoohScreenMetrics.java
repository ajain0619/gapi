package com.nexage.app.metric;

import com.codahale.metrics.MetricRegistry;
import com.ssp.geneva.common.base.annotation.Incubating;
import org.coursera.metrics.datadog.TaggedName;
import org.springframework.stereotype.Component;

/** */
@Incubating(
    name =
        "This class and package is to be moved in ticket SSP-28028 to be placed in its own module. Other classes utilizing metrics should not be placed in this package as this will be removed.")
@Component
public class DoohScreenMetrics {

  private static final String DOOH_METRIC_ROOT = "dooh.screen.management";
  private static final String SELLER_PID = "seller_pid";
  private final MetricRegistry metricRegistry;

  public DoohScreenMetrics(MetricRegistry metricRegistry) {
    this.metricRegistry = metricRegistry;
  }

  /**
   * Record created screen count value metric and tag with seller pid
   *
   * @param numberOfScreens number of screens created
   * @param sellerPid
   */
  public void recordCreatedScreenCount(int numberOfScreens, Long sellerPid) {
    var metricScreenName =
        new TaggedName.TaggedNameBuilder()
            .metricName(MetricRegistry.name(DOOH_METRIC_ROOT, "created", "histogram"))
            .addTag(SELLER_PID, String.valueOf(sellerPid))
            .build()
            .encode();
    metricRegistry.histogram(metricScreenName).update(numberOfScreens);
  }

  /**
   * Increment metric counter for file upload success and tag with seller pid
   *
   * @param sellerPid
   */
  public void incrementFileUploadSuccess(Long sellerPid) {
    var metricScreenName =
        new TaggedName.TaggedNameBuilder()
            .metricName(MetricRegistry.name(DOOH_METRIC_ROOT, "file", "success", "count"))
            .addTag(SELLER_PID, String.valueOf(sellerPid))
            .build()
            .encode();
    metricRegistry.counter(metricScreenName).inc();
  }

  /**
   * Increment metric for file upload error and tag with seller pid
   *
   * @param sellerPid
   */
  public void incrementFileUploadError(Long sellerPid) {
    var metricScreenName =
        new TaggedName.TaggedNameBuilder()
            .metricName(MetricRegistry.name(DOOH_METRIC_ROOT, "file", "error", "count"))
            .addTag(SELLER_PID, String.valueOf(sellerPid))
            .build()
            .encode();
    metricRegistry.counter(metricScreenName).inc();
  }
}
