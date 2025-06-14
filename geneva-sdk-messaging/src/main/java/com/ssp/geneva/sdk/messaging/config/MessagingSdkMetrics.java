package com.ssp.geneva.sdk.messaging.config;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import lombok.Getter;

@Getter
public class MessagingSdkMetrics {
  private final Counter messagesNotMatchedCounter;
  private final Counter exceptionThrownCounter;
  private final Counter messagesHandledCounter;
  private final Timer messageHandledTimer;
  private final Counter messagesPublishedCounter;
  private final Timer messagePublishedTimer;
  private final Counter messagesFailedToBePublishedCounter;

  public MessagingSdkMetrics(
      MetricRegistry globalMetricRegistry, MessagingSdkProperties properties) {
    MetricRegistry messagingSdkMetricRegistry = new MetricRegistry();
    messagesNotMatchedCounter = messagingSdkMetricRegistry.counter("messages.not.matched.count");
    exceptionThrownCounter = messagingSdkMetricRegistry.counter("exception.thrown.count");
    messagesHandledCounter = messagingSdkMetricRegistry.counter("messages.handled.count");
    messageHandledTimer = messagingSdkMetricRegistry.timer("message.handled.time");
    messagesPublishedCounter = messagingSdkMetricRegistry.counter("messages.published.count");
    messagePublishedTimer = messagingSdkMetricRegistry.timer("message.published.time");
    messagesFailedToBePublishedCounter =
        messagingSdkMetricRegistry.counter("messages.failed.to.be.published.count");
    globalMetricRegistry.registerAll(properties.getMetricsPrefix(), messagingSdkMetricRegistry);
  }
}
