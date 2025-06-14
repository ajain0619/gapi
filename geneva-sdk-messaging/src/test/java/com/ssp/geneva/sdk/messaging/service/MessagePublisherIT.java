package com.ssp.geneva.sdk.messaging.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkConfig;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkProperties;
import com.ssp.geneva.sdk.messaging.config.TestConfig;
import com.ssp.geneva.sdk.messaging.model.MessageContainer;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import com.ssp.geneva.sdk.messaging.model.Topic;
import com.ssp.geneva.sdk.messaging.stub.QueueListenerStub;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class, MessagingSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class MessagePublisherIT {

  @Autowired private AmazonSNS amazonSNS;

  @Autowired private AmazonSQS amazonSQS;

  @Autowired private MessagePublisher messagePublisher;

  @Autowired private RedisTemplate redisTemplate;

  @Autowired private MessagingSdkProperties messagingSdkProperties;

  @Autowired private ObjectMapper objectMapper;

  private String queueUrl;

  @BeforeEach
  void setUp() {
    final CreateTopicResult createTopicResult =
        amazonSNS.createTopic("geneva-messaging-dev-" + Topic.PLACEMENT.getName());
    final CreateQueueResult createQueueResult = amazonSQS.createQueue("subscribed-queue");
    final SubscribeRequest subscribeRequest =
        new SubscribeRequest()
            .withTopicArn(createTopicResult.getTopicArn())
            .withProtocol("sqs")
            .withEndpoint(createQueueResult.getQueueUrl())
            .addAttributesEntry("RawMessageDelivery", Boolean.TRUE.toString());
    amazonSNS.subscribe(subscribeRequest);
    queueUrl = createQueueResult.getQueueUrl();
    final Jackson2JsonRedisSerializer hashValueSerializer =
        new Jackson2JsonRedisSerializer(MessageContainer.class);
    hashValueSerializer.setObjectMapper(objectMapper);
    redisTemplate.setHashValueSerializer(hashValueSerializer);
  }

  @Test
  void publishMessageSuccessfully() throws InterruptedException, JsonProcessingException {
    final MessageHeaders messageHeaders =
        MessageHeaders.builder().withSubOperation("message_event").build();
    final QueueListenerStub.MessageSample messageSample =
        QueueListenerStub.MessageSample.builder().message("hi").build();
    messagePublisher.publish(Topic.PLACEMENT, messageSample, messageHeaders);

    Thread.sleep(500);

    final ReceiveMessageRequest receiveMessageRequest =
        new ReceiveMessageRequest()
            .withQueueUrl(queueUrl)
            .withMessageAttributeNames(MessageHeaders.SUB_OPERATION_HEADER);
    final ReceiveMessageResult receiveMessageResult =
        amazonSQS.receiveMessage(receiveMessageRequest);

    assertEquals(1, receiveMessageResult.getMessages().size());
    final Message message = receiveMessageResult.getMessages().get(0);
    assertEquals(objectMapper.writeValueAsString(messageSample), message.getBody());
    assertTrue(message.getMessageAttributes().containsKey(MessageHeaders.SUB_OPERATION_HEADER));
  }

  @Test
  void whenPublishMessageWithCorrelationId_thenMessagePublishedAndStoredInRepository()
      throws InterruptedException, JsonProcessingException {
    final MessageHeaders messageHeaders =
        MessageHeaders.builder()
            .withCorrelationId(UUID.randomUUID().toString())
            .withSubOperation("message_event")
            .build();
    final QueueListenerStub.MessageSample messageSample =
        QueueListenerStub.MessageSample.builder().message("hi").build();
    messagePublisher.publish(Topic.PLACEMENT, messageSample, messageHeaders);

    Thread.sleep(500);

    final ReceiveMessageRequest receiveMessageRequest =
        new ReceiveMessageRequest()
            .withQueueUrl(queueUrl)
            .withMessageAttributeNames(
                MessageHeaders.CORRELATION_ID_HEADER, MessageHeaders.SUB_OPERATION_HEADER);
    final ReceiveMessageResult receiveMessageResult =
        amazonSQS.receiveMessage(receiveMessageRequest);

    assertEquals(1, receiveMessageResult.getMessages().size());
    final Message message = receiveMessageResult.getMessages().get(0);
    assertEquals(objectMapper.writeValueAsString(messageSample), message.getBody());
    assertTrue(message.getMessageAttributes().containsKey(MessageHeaders.SUB_OPERATION_HEADER));
    assertTrue(message.getMessageAttributes().containsKey(MessageHeaders.CORRELATION_ID_HEADER));

    final MessageContainer messageContainer =
        (MessageContainer)
            redisTemplate
                .opsForHash()
                .get(
                    messagingSdkProperties.getAdjustedStoreKey(),
                    messageHeaders.getCorrelationId());
    assertNotNull(messageContainer);
    assertEquals(objectMapper.writeValueAsString(messageSample), messageContainer.getMessage());
    assertEquals(
        Integer.valueOf(messagingSdkProperties.getRetryAttempts()), messageContainer.getRetry());
    assertTrue(
        messageHeaders.toMap().entrySet().containsAll(messageContainer.getHeaders().entrySet()));
    assertTrue(messageContainer.getTopic().endsWith(Topic.PLACEMENT.getName()));
  }
}
