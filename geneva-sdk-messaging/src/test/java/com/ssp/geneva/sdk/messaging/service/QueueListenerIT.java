package com.ssp.geneva.sdk.messaging.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkConfig;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkJacksonBeanFactory;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkProperties;
import com.ssp.geneva.sdk.messaging.config.TestConfig;
import com.ssp.geneva.sdk.messaging.model.MessageContainer;
import com.ssp.geneva.sdk.messaging.stub.FilteredQueueListenerStub;
import com.ssp.geneva.sdk.messaging.stub.QueueListenerStub;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeType;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class, MessagingSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class QueueListenerIT {

  private static final Integer TIMEOUT = 5000;

  @Autowired private AmazonSQS amazonSQS;

  @Autowired private QueueListenerStub queueListenerStub;

  @Autowired private FilteredQueueListenerStub filteredQueueListenerStub;

  @Autowired private RedisTemplate redisTemplate;

  @Autowired private MessagingSdkProperties messagingSdkProperties;

  private String queueUrl;

  private String filteredQueueUrl;

  private final ObjectMapper objectMapper = MessagingSdkJacksonBeanFactory.initObjectMapper();

  @BeforeEach
  void setUp() {
    final GetQueueUrlResult getQueueUrlResult =
        amazonSQS.getQueueUrl(messagingSdkProperties.getMessagingPrefix() + "-queue");
    queueUrl = getQueueUrlResult.getQueueUrl();
    final GetQueueUrlResult getFilteredQueueUrlResult =
        amazonSQS.getQueueUrl(messagingSdkProperties.getMessagingPrefix() + "-filtered_queue");
    filteredQueueUrl = getFilteredQueueUrlResult.getQueueUrl();
  }

  @Test
  void receiveMessageSuccessfully()
      throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {
    final QueueListenerStub.MessageSample messageSample =
        new QueueListenerStub.MessageSample("hello localstack");
    final MessageAttributeValue contentType =
        new MessageAttributeValue().withDataType("String").withStringValue("application/json");
    final SendMessageRequest sendMessageRequest =
        new SendMessageRequest()
            .withQueueUrl(queueUrl)
            .withMessageBody(objectMapper.writeValueAsString(messageSample))
            .withMessageAttributes(Map.of(MessageHeaders.CONTENT_TYPE, contentType));

    CompletableFuture<QueueListenerStub.MessageSample> messageCompletableFuture =
        new CompletableFuture<>();
    queueListenerStub.setMessageCallback(e -> messageCompletableFuture.complete(e));

    CompletableFuture<Map<String, Object>> headersCompletableFuture = new CompletableFuture<>();
    queueListenerStub.setHeadersCallback(e -> headersCompletableFuture.complete(e));

    amazonSQS.sendMessage(sendMessageRequest);

    CompletableFuture.allOf(messageCompletableFuture, headersCompletableFuture)
        .get(TIMEOUT, TimeUnit.MILLISECONDS);

    QueueListenerStub.MessageSample receivedMessage = messageCompletableFuture.get();
    Map<String, Object> receivedHeaders = headersCompletableFuture.get();

    assertEquals(messageSample, receivedMessage);
    assertTrue(receivedHeaders.containsKey(MessageHeaders.CONTENT_TYPE));
    assertEquals(
        MimeType.valueOf("application/json"), receivedHeaders.get(MessageHeaders.CONTENT_TYPE));
  }

  @Test
  void receiveFilteredMessagesSuccessfully()
      throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {
    final FilteredQueueListenerStub.CreateMessage createMessage =
        new FilteredQueueListenerStub.CreateMessage("create");
    final MessageAttributeValue contentType =
        new MessageAttributeValue().withDataType("String").withStringValue("application/json");
    final MessageAttributeValue createSubOperation =
        new MessageAttributeValue().withDataType("String").withStringValue("create");
    final SendMessageRequest sendCreateMessageRequest =
        new SendMessageRequest()
            .withQueueUrl(filteredQueueUrl)
            .withMessageBody(objectMapper.writeValueAsString(createMessage))
            .withMessageAttributes(
                Map.of(
                    MessageHeaders.CONTENT_TYPE,
                    contentType,
                    "h-subOperation",
                    createSubOperation));

    CompletableFuture<FilteredQueueListenerStub.CreateMessage> createMessageCompletableFuture =
        new CompletableFuture<>();
    filteredQueueListenerStub.setCreateMessageCallback(
        e -> createMessageCompletableFuture.complete(e));

    CompletableFuture<Map<String, Object>> createHeadersCompletableFuture =
        new CompletableFuture<>();
    filteredQueueListenerStub.setCreateHeadersCallback(
        e -> createHeadersCompletableFuture.complete(e));

    amazonSQS.sendMessage(sendCreateMessageRequest);

    final FilteredQueueListenerStub.UpdateMessage updateMessage =
        new FilteredQueueListenerStub.UpdateMessage("update", new Date());
    final MessageAttributeValue updateSubOperation =
        new MessageAttributeValue().withDataType("String").withStringValue("update");
    final SendMessageRequest sendUpdateMessageRequest =
        new SendMessageRequest()
            .withQueueUrl(filteredQueueUrl)
            .withMessageBody(objectMapper.writeValueAsString(updateMessage))
            .withMessageAttributes(
                Map.of(
                    MessageHeaders.CONTENT_TYPE,
                    contentType,
                    "h-subOperation",
                    updateSubOperation));

    CompletableFuture<FilteredQueueListenerStub.UpdateMessage> updateMessageCompletableFuture =
        new CompletableFuture<>();
    filteredQueueListenerStub.setUpdateMessageCallback(
        e -> updateMessageCompletableFuture.complete(e));

    CompletableFuture<Map<String, Object>> updateHeadersCompletableFuture =
        new CompletableFuture<>();
    filteredQueueListenerStub.setUpdateHeadersCallback(
        e -> updateHeadersCompletableFuture.complete(e));

    amazonSQS.sendMessage(sendUpdateMessageRequest);

    CompletableFuture.allOf(
            createMessageCompletableFuture,
            createHeadersCompletableFuture,
            updateMessageCompletableFuture,
            updateHeadersCompletableFuture)
        .get(TIMEOUT, TimeUnit.MILLISECONDS);

    FilteredQueueListenerStub.CreateMessage receivedCreateMessage =
        createMessageCompletableFuture.get();
    Map<String, Object> receivedCreateHeaders = createHeadersCompletableFuture.get();

    FilteredQueueListenerStub.UpdateMessage receivedUpdateMessage =
        updateMessageCompletableFuture.get();
    Map<String, Object> receivedUpdateHeaders = updateHeadersCompletableFuture.get();

    assertEquals(createMessage, receivedCreateMessage);
    assertEquals(updateMessage, receivedUpdateMessage);
    assertTrue(receivedCreateHeaders.containsValue("create"));
    assertTrue(receivedUpdateHeaders.containsValue("update"));
  }

  @Test
  void whenMessageWithReplyCorrelationId_thenRemoveFromRepository()
      throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {
    final MessageAttributeValue replyCorrelationId =
        new MessageAttributeValue()
            .withDataType("String")
            .withStringValue(UUID.randomUUID().toString());

    final MessageContainer messageContainer = MessageContainer.builder().build();

    redisTemplate
        .opsForHash()
        .putIfAbsent(
            messagingSdkProperties.getAdjustedStoreKey(),
            replyCorrelationId.getStringValue(),
            messageContainer);

    final QueueListenerStub.MessageSample messageSample =
        new QueueListenerStub.MessageSample("hello localstack");
    final MessageAttributeValue contentType =
        new MessageAttributeValue().withDataType("String").withStringValue("application/json");
    final SendMessageRequest sendMessageRequest =
        new SendMessageRequest()
            .withQueueUrl(queueUrl)
            .withMessageBody(objectMapper.writeValueAsString(messageSample))
            .withMessageAttributes(
                Map.of(
                    MessageHeaders.CONTENT_TYPE,
                    contentType,
                    com.ssp.geneva.sdk.messaging.model.MessageHeaders.REPLY_CORRELATION_ID_HEADER,
                    replyCorrelationId));

    CompletableFuture<QueueListenerStub.MessageSample> messageCompletableFuture =
        new CompletableFuture<>();
    queueListenerStub.setMessageCallback(e -> messageCompletableFuture.complete(e));

    CompletableFuture<Map<String, Object>> headersCompletableFuture = new CompletableFuture<>();
    queueListenerStub.setHeadersCallback(e -> headersCompletableFuture.complete(e));

    amazonSQS.sendMessage(sendMessageRequest);

    CompletableFuture.allOf(messageCompletableFuture, headersCompletableFuture)
        .get(TIMEOUT, TimeUnit.MILLISECONDS);

    QueueListenerStub.MessageSample receivedMessage = messageCompletableFuture.get();
    Map<String, Object> receivedHeaders = headersCompletableFuture.get();

    assertEquals(messageSample, receivedMessage);
    assertTrue(receivedHeaders.containsKey(MessageHeaders.CONTENT_TYPE));
    assertEquals(
        MimeType.valueOf("application/json"), receivedHeaders.get(MessageHeaders.CONTENT_TYPE));

    boolean keyExist = true;
    Instant start = Instant.now();
    while (keyExist) {
      keyExist =
          redisTemplate
              .opsForHash()
              .hasKey(
                  messagingSdkProperties.getAdjustedStoreKey(),
                  replyCorrelationId.getStringValue());

      if (Instant.now().minus(TIMEOUT, ChronoUnit.MILLIS).isAfter(start))
        throw new TimeoutException();

      Thread.sleep(100);
    }
  }
}
