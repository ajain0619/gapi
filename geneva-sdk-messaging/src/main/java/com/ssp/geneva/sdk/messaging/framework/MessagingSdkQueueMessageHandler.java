package com.ssp.geneva.sdk.messaging.framework;

import com.codahale.metrics.Timer;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkMetrics;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkProperties;
import com.ssp.geneva.sdk.messaging.framework.annotation.QueueListener;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.HandlerMethod;

@Log4j2
public class MessagingSdkQueueMessageHandler
    extends org.springframework.cloud.aws.messaging.listener.QueueMessageHandler {

  private final MessagingSdkProperties messagingSdkProperties;
  private final RedisTemplate redisTemplate;
  private final MessagingSdkMetrics messagingSdkMetrics;

  public MessagingSdkQueueMessageHandler(
      List<MessageConverter> messageConverters,
      MessagingSdkProperties messagingSdkProperties,
      RedisTemplate redisTemplate,
      MessagingSdkMetrics messagingSdkMetrics) {
    super(messageConverters);
    this.messagingSdkProperties = messagingSdkProperties;
    this.redisTemplate = redisTemplate;
    this.messagingSdkMetrics = messagingSdkMetrics;
  }

  @Override
  protected MappingInformation getMappingForMethod(Method method, Class<?> handlerType) {
    final QueueListener queueListener = AnnotationUtils.findAnnotation(method, QueueListener.class);

    if (queueListener != null && queueListener.value().length > 0) {
      return new MessagingSdkMappingInformation(
          resolveNames(queueListener.value()),
          queueListener.policy(),
          resolveFilter(queueListener.filter()));
    }
    return null;
  }

  @Override
  protected MappingInformation getMatchingMapping(MappingInformation mapping, Message<?> message) {
    final MessagingSdkMappingInformation messagingSdkMappingInformation =
        MessagingSdkMappingInformation.class.cast(mapping);
    return messagingSdkMappingInformation.getLogicalResourceIds().contains(getDestination(message))
            && message
                .getHeaders()
                .entrySet()
                .containsAll(messagingSdkMappingInformation.getFilters().entrySet())
        ? mapping
        : null;
  }

  @Override
  protected void handleMessageInternal(Message<?> message, String lookupDestination) {
    super.handleMessageInternal(message, lookupDestination);
    if (redisTemplate != null) {
      final String replyCorrelationId =
          (String) message.getHeaders().get(MessageHeaders.REPLY_CORRELATION_ID_HEADER);

      if (replyCorrelationId != null) {
        log.info(
            "Removing message with replyCorrelationId `{}` from repository", replyCorrelationId);
        try {
          redisTemplate
              .opsForHash()
              .delete(messagingSdkProperties.getAdjustedStoreKey(), replyCorrelationId);
          log.info(
              "Message with replyCorrelationId `{}` was removed from repository",
              replyCorrelationId);
        } catch (Exception e) {
          log.warn(
              "Failed to remove message with replyCorrelationId `{}` from repository",
              replyCorrelationId,
              e);
        }
      }
    }
  }

  @Override
  protected void handleNoMatch(
      Set<MappingInformation> ts, String lookupDestination, Message<?> message) {
    super.handleNoMatch(ts, lookupDestination, message);
    messagingSdkMetrics.getMessagesNotMatchedCounter().inc();
  }

  @Override
  protected void handleMatch(
      MappingInformation mapping,
      HandlerMethod handlerMethod,
      String lookupDestination,
      Message<?> message) {
    final Timer.Context context = messagingSdkMetrics.getMessageHandledTimer().time();
    try {
      super.handleMatch(mapping, handlerMethod, lookupDestination, message);
      messagingSdkMetrics.getMessagesHandledCounter().inc();
    } catch (Exception e) {
      messagingSdkMetrics.getExceptionThrownCounter().inc();
      throw e;
    } finally {
      context.close();
    }
  }

  private Set<String> resolveNames(String[] names) {
    return Arrays.stream(names)
        .map(name -> messagingSdkProperties.getMessagingPrefix() + "-" + name)
        .collect(Collectors.toSet());
  }

  private Map<String, String> resolveFilter(String[] filters) {
    Map<String, String> filtersMap = new HashMap<>();
    if (filters != null && filters.length > 0) {
      Arrays.stream(filters)
          .forEach(
              filter -> {
                final String[] split = filter.split("=");

                if (split.length == 2) {
                  filtersMap.put(split[0].trim(), split[1].trim());
                }
              });
    }

    return filtersMap;
  }

  protected static class MessagingSdkMappingInformation
      extends QueueMessageHandler.MappingInformation {
    private final Map<String, String> filters;

    public MessagingSdkMappingInformation(
        Set<String> logicalResourceIds,
        SqsMessageDeletionPolicy deletionPolicy,
        Map<String, String> filters) {
      super(logicalResourceIds, deletionPolicy);
      this.filters = filters;
    }

    public Map<String, String> getFilters() {
      return filters;
    }
  }
}
