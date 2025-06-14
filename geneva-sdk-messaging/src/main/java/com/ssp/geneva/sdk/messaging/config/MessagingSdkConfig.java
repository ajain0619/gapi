package com.ssp.geneva.sdk.messaging.config;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.messaging.framework.MessagingSdkQueueMessageHandlerFactory;
import com.ssp.geneva.sdk.messaging.service.MessagePublisher;
import java.util.Arrays;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.aws.context.config.annotation.ContextDefaultConfigurationRegistrar;
import org.springframework.cloud.aws.core.env.ResourceIdResolver;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.util.CollectionUtils;

@Configuration
@Import(ContextDefaultConfigurationRegistrar.class)
public class MessagingSdkConfig {

  @Autowired(required = false)
  private final SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory =
      new SimpleMessageListenerContainerFactory();

  @Value("${messaging.environment.prefix}")
  private String messagingPrefix;

  @Value("${messaging.retry.attempts}")
  private int retryAttempts;

  @Value("${messaging.store.key}")
  private String storeKey;

  @Value("${messaging.metrics.prefix:geneva.sdk.messaging}")
  private String metricsPrefix;

  @Bean
  public MessagingSdkProperties messagingSdkProperties() {
    return MessagingSdkProperties.builder()
        .messagingPrefix(messagingPrefix)
        .retryAttempts(retryAttempts)
        .storeKey(storeKey)
        .metricsPrefix(metricsPrefix)
        .build();
  }

  @Bean
  public MessagingSdkMetrics messagingSdkMetrics(
      @Autowired MetricRegistry metricRegistry, @Autowired MessagingSdkProperties properties) {
    return new MessagingSdkMetrics(metricRegistry, properties);
  }

  @Bean
  public RedisTemplate redisTemplate(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
          @Autowired(required = false)
          RedisConnectionFactory redisConnectionFactory,
      @Qualifier("messagingSdkObjectMapper") ObjectMapper objectMapper) {
    if (redisConnectionFactory != null) {
      final RedisTemplate redisTemplate = new RedisTemplate();
      redisTemplate.setConnectionFactory(redisConnectionFactory);
      redisTemplate.setKeySerializer(new StringRedisSerializer());
      redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
      return redisTemplate;
    }
    return null;
  }

  @Bean
  public MessagingSdkQueueMessageHandlerFactory queueMessageHandlerFactory(
      MessagingSdkProperties messagingSdkProperties,
      MappingJackson2MessageConverter mappingJackson2MessageConverter,
      @Autowired(required = false) RedisTemplate redisTemplate,
      MessagingSdkMetrics messagingSdkMetrics) {
    return new MessagingSdkQueueMessageHandlerFactory(
        messagingSdkProperties,
        mappingJackson2MessageConverter,
        redisTemplate,
        messagingSdkMetrics);
  }

  @Bean
  public SimpleMessageListenerContainer simpleMessageListenerContainer(
      AmazonSQSAsync amazonSqs,
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
          ResourceIdResolver resourceIdResolver,
      QueueMessageHandler queueMessageHandler) {
    if (this.simpleMessageListenerContainerFactory.getAmazonSqs() == null) {
      this.simpleMessageListenerContainerFactory.setAmazonSqs(amazonSqs);
    }
    if (this.simpleMessageListenerContainerFactory.getResourceIdResolver() == null) {
      this.simpleMessageListenerContainerFactory.setResourceIdResolver(resourceIdResolver);
    }

    SimpleMessageListenerContainer simpleMessageListenerContainer =
        this.simpleMessageListenerContainerFactory.createSimpleMessageListenerContainer();
    simpleMessageListenerContainer.setMessageHandler(queueMessageHandler);
    return simpleMessageListenerContainer;
  }

  @Bean
  public QueueMessageHandler queueMessageHandler(
      AmazonSQSAsync amazonSqs,
      MessagingSdkQueueMessageHandlerFactory messagingSdkQueueMessageHandlerFactory,
      BeanFactory beanFactory,
      MappingJackson2MessageConverter mappingJackson2MessageConverter) {
    if (this.simpleMessageListenerContainerFactory.getQueueMessageHandler() != null) {
      return this.simpleMessageListenerContainerFactory.getQueueMessageHandler();
    } else {
      return getMessageHandler(
          amazonSqs,
          messagingSdkQueueMessageHandlerFactory,
          beanFactory,
          mappingJackson2MessageConverter);
    }
  }

  @Bean("messagingSdkObjectMapper")
  public ObjectMapper objectMapper() {
    return MessagingSdkJacksonBeanFactory.initObjectMapper();
  }

  @Bean
  @ConditionalOnBean(name = {"messagingSdkObjectMapper"})
  public MessagePublisher messagePublisher(
      AmazonSNS amazonSNS,
      MessagingSdkProperties messagingSdkProperties,
      @Autowired(required = false) RedisTemplate redisTemplate,
      @Qualifier("messagingSdkObjectMapper") ObjectMapper objectMapper,
      MessagingSdkMetrics messagingSdkMetrics) {
    return new MessagePublisher(
        amazonSNS, messagingSdkProperties, redisTemplate, objectMapper, messagingSdkMetrics);
  }

  @Bean
  @ConditionalOnMissingBean
  public MappingJackson2MessageConverter mappingJackson2MessageConverter() {
    MappingJackson2MessageConverter jacksonMessageConverter = new MappingJackson2MessageConverter();
    jacksonMessageConverter.setSerializedPayloadClass(String.class);
    jacksonMessageConverter.setStrictContentTypeMatch(true);
    return jacksonMessageConverter;
  }

  private QueueMessageHandler getMessageHandler(
      AmazonSQSAsync amazonSqs,
      MessagingSdkQueueMessageHandlerFactory messagingSdkQueueMessageHandlerFactory,
      BeanFactory beanFactory,
      MappingJackson2MessageConverter mappingJackson2MessageConverter) {
    if (messagingSdkQueueMessageHandlerFactory.getAmazonSqs() == null) {
      messagingSdkQueueMessageHandlerFactory.setAmazonSqs(amazonSqs);
    }

    if (CollectionUtils.isEmpty(messagingSdkQueueMessageHandlerFactory.getMessageConverters())
        && mappingJackson2MessageConverter != null) {
      messagingSdkQueueMessageHandlerFactory.setMessageConverters(
          Arrays.asList(mappingJackson2MessageConverter));
    }

    messagingSdkQueueMessageHandlerFactory.setBeanFactory(beanFactory);
    return messagingSdkQueueMessageHandlerFactory.createQueueMessageHandler();
  }
}
