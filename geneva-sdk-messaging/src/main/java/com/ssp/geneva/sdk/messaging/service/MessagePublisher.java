package com.ssp.geneva.sdk.messaging.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkMetrics;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkProperties;
import com.ssp.geneva.sdk.messaging.model.MessageContainer;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import com.ssp.geneva.sdk.messaging.model.Topic;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;

@Log4j2
public class MessagePublisher {
  private final NotificationMessagingTemplate notificationMessagingTemplate;

  private final MessagingSdkProperties messagingSdkProperties;

  private final RedisTemplate redisTemplate;

  private final ObjectMapper objectMapper;

  private final MessagingSdkMetrics messagingSdkMetrics;

  public MessagePublisher(
      AmazonSNS amazonSNS,
      MessagingSdkProperties messagingSdkProperties,
      RedisTemplate redisTemplate,
      @Qualifier("messagingSdkObjectMapper") ObjectMapper objectMapper,
      MessagingSdkMetrics messagingSdkMetrics) {
    this.notificationMessagingTemplate = new NotificationMessagingTemplate(amazonSNS);
    this.messagingSdkProperties = messagingSdkProperties;
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
    this.messagingSdkMetrics = messagingSdkMetrics;
  }

  /**
   * Publish a message to an SNS topic
   *
   * @param topic topic name
   * @param message message
   * @param headers message's headers
   */
  public void publish(Topic topic, Object message, @Nullable MessageHeaders headers) {
    Objects.requireNonNull(topic, () -> "topic must not be null");
    Objects.requireNonNull(message, () -> "message must not be null");
    final Timer.Context context = messagingSdkMetrics.getMessagePublishedTimer().time();
    try {
      publishInternal(topic, message, headers);
      messagingSdkMetrics.getMessagesPublishedCounter().inc();
    } catch (Exception e) {
      messagingSdkMetrics.getMessagesFailedToBePublishedCounter().inc();
      throw e;
    } finally {
      context.close();
    }
  }

  private void publishInternal(Topic topic, Object message, MessageHeaders headers) {
    final String resolveTopicName = resolveTopicName(topic);
    final Map<String, Object> messageHeaders = headers != null ? headers.toMap() : new HashMap<>();
    this.notificationMessagingTemplate.convertAndSend(
        resolveTopicName,
        message,
        new org.springframework.messaging.MessageHeaders(messageHeaders));

    updateRepositoryIfNecessary(resolveTopicName, message, messageHeaders);
  }

  private void updateRepositoryIfNecessary(
      String resolveTopicName, Object message, Map<String, Object> messageHeaders) {
    if (redisTemplate != null) {
      final String correlationId =
          (String) messageHeaders.get(MessageHeaders.CORRELATION_ID_HEADER);

      if (correlationId != null) {
        final MessageContainer messageContainer;
        try {
          messageContainer =
              MessageContainer.builder()
                  .headers(messageHeaders)
                  .message(objectMapper.writeValueAsString(message))
                  .retry(messagingSdkProperties.getRetryAttempts())
                  .timestamp(Instant.now())
                  .topic(resolveTopicName)
                  .build();

          redisTemplate
              .opsForHash()
              .putIfAbsent(
                  messagingSdkProperties.getAdjustedStoreKey(), correlationId, messageContainer);
        } catch (Exception e) {
          final String warningMessage =
              String.format(
                  "Failed to store message `%s` with correlationId `%s'", message, correlationId);
          log.warn(warningMessage, e);
        }
      }
    }
  }

  private String resolveTopicName(Topic topic) {
    return messagingSdkProperties.getMessagingPrefix() + "-" + topic.getName();
  }
}
