package com.ssp.geneva.sdk.messaging.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ssp.geneva.sdk.messaging.framework.MessagingSdkQueueMessageHandlerFactory;
import com.ssp.geneva.sdk.messaging.service.MessagePublisher;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@Testcontainers
@ContextConfiguration(classes = {TestConfig.class, MessagingSdkConfig.class})
@TestPropertySource({"classpath:application-test.properties"})
class MessagingSdkConfigIT {

  @Autowired private ApplicationContext applicationContext;

  @Test
  void testApplicationPropertiesSet() throws IOException {
    final Environment environment = applicationContext.getEnvironment();
    final String environmentPrefix = environment.getProperty("messaging.environment.prefix");
    final String retryAttempts = environment.getProperty("messaging.retry.attempts");
    final String storeKey = environment.getProperty("messaging.store.key");
    final String metricsPrefix = environment.getProperty("messaging.metrics.prefix");

    assertEquals("geneva-messaging-dev", environmentPrefix);
    assertEquals(3, Integer.valueOf(retryAttempts).intValue());
    assertEquals("messagesStore", storeKey);
    assertEquals("geneva.sdk.messaging", metricsPrefix);
  }

  @Test
  void testBeansSet() {
    assertNotNull(applicationContext.getBean(MessagingSdkProperties.class));
    assertNotNull(applicationContext.getBean(MessagePublisher.class));
    assertNotNull(applicationContext.getBean(MessagingSdkQueueMessageHandlerFactory.class));
    assertNotNull(applicationContext.getBean(SimpleMessageListenerContainer.class));
    assertNotNull(applicationContext.getBean(QueueMessageHandler.class));
    assertNotNull(applicationContext.getBean(MappingJackson2MessageConverter.class));
    assertNotNull(applicationContext.getBean(RedisTemplate.class));
    assertNotNull(applicationContext.getBean(MessagingSdkMetrics.class));
  }
}
