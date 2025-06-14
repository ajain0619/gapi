package com.ssp.geneva.sdk.messaging.framework;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkMetrics;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkProperties;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.aws.core.env.ResourceIdResolver;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SendToHandlerMethodReturnValueHandler;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.core.DestinationResolvingMessageSendingOperations;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.util.CollectionUtils;

@Data
public class MessagingSdkQueueMessageHandlerFactory {

  private List<HandlerMethodArgumentResolver> argumentResolvers;
  private List<HandlerMethodReturnValueHandler> returnValueHandlers;
  private DestinationResolvingMessageSendingOperations<?> sendToMessagingTemplate;
  private AmazonSQSAsync amazonSqs;
  private ResourceIdResolver resourceIdResolver;
  private BeanFactory beanFactory;
  private List<MessageConverter> messageConverters;

  private final MessagingSdkProperties messagingSdkProperties;
  private final MappingJackson2MessageConverter mappingJackson2MessageConverter;
  private final RedisTemplate redisTemplate;
  private final MessagingSdkMetrics messagingSdkMetrics;

  public MessagingSdkQueueMessageHandlerFactory(
      MessagingSdkProperties messagingSdkProperties,
      MappingJackson2MessageConverter mappingJackson2MessageConverter,
      RedisTemplate redisTemplate,
      MessagingSdkMetrics messagingSdkMetrics) {
    this.messagingSdkProperties = messagingSdkProperties;
    this.mappingJackson2MessageConverter = mappingJackson2MessageConverter;
    this.redisTemplate = redisTemplate;
    this.messagingSdkMetrics = messagingSdkMetrics;
  }

  public org.springframework.cloud.aws.messaging.listener.QueueMessageHandler
      createQueueMessageHandler() {
    MessagingSdkQueueMessageHandler messagingSdkQueueMessageHandler =
        new MessagingSdkQueueMessageHandler(
            CollectionUtils.isEmpty(this.messageConverters)
                ? List.of(mappingJackson2MessageConverter)
                : this.messageConverters,
            messagingSdkProperties,
            redisTemplate,
            messagingSdkMetrics);

    if (!CollectionUtils.isEmpty(this.argumentResolvers)) {
      messagingSdkQueueMessageHandler.getCustomArgumentResolvers().addAll(this.argumentResolvers);
    }
    if (!CollectionUtils.isEmpty(this.returnValueHandlers)) {
      messagingSdkQueueMessageHandler
          .getCustomReturnValueHandlers()
          .addAll(this.returnValueHandlers);
    }

    SendToHandlerMethodReturnValueHandler sendToHandlerMethodReturnValueHandler;
    if (this.sendToMessagingTemplate != null) {
      sendToHandlerMethodReturnValueHandler =
          new SendToHandlerMethodReturnValueHandler(this.sendToMessagingTemplate);
    } else {
      sendToHandlerMethodReturnValueHandler =
          new SendToHandlerMethodReturnValueHandler(
              getDefaultSendToQueueMessagingTemplate(this.amazonSqs, this.resourceIdResolver));
    }
    sendToHandlerMethodReturnValueHandler.setBeanFactory(this.beanFactory);
    messagingSdkQueueMessageHandler
        .getCustomReturnValueHandlers()
        .add(sendToHandlerMethodReturnValueHandler);

    return messagingSdkQueueMessageHandler;
  }

  private QueueMessagingTemplate getDefaultSendToQueueMessagingTemplate(
      AmazonSQSAsync amazonSqs, ResourceIdResolver resourceIdResolver) {
    return new QueueMessagingTemplate(
        amazonSqs, resourceIdResolver, mappingJackson2MessageConverter);
  }
}
