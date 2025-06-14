package com.ssp.geneva.sdk.messaging.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkMetrics;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkProperties;
import com.ssp.geneva.sdk.messaging.model.MessageContainer;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import com.ssp.geneva.sdk.messaging.model.Topic;
import com.ssp.geneva.sdk.messaging.stub.QueueListenerStub;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MessagePublisherTest {
  private static final String MESSAGING_PREFIX = "geneva-messaging-dev-";

  @Mock private NotificationMessagingTemplate notificationMessagingTemplate;

  @Spy
  private MessagingSdkProperties messagingSdkProperties =
      MessagingSdkProperties.builder()
          .messagingPrefix("geneva-messaging-dev")
          .retryAttempts(3)
          .storeKey("messagesStore")
          .build();

  @Mock private RedisTemplate redisTemplate;

  @Spy private ObjectMapper objectMapper = new ObjectMapper();

  @Mock private MessagingSdkMetrics messagingSdkMetrics;

  @InjectMocks private MessagePublisher messagePublisher;

  private final Timer timerMock = mock(Timer.class);
  private final Timer.Context contextMock = mock(Timer.Context.class);
  private final Counter publishSuccessCounterMock = mock(Counter.class);
  private final Counter publishFailureCounterMock = mock(Counter.class);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        messagePublisher, "notificationMessagingTemplate", notificationMessagingTemplate);
    lenient().when(messagingSdkMetrics.getMessagePublishedTimer()).thenReturn(timerMock);
    lenient().when(timerMock.time()).thenReturn(contextMock);
    lenient()
        .when(messagingSdkMetrics.getMessagesPublishedCounter())
        .thenReturn(publishSuccessCounterMock);
    lenient()
        .when(messagingSdkMetrics.getMessagesFailedToBePublishedCounter())
        .thenReturn(publishFailureCounterMock);
  }

  @Test
  void publishMessageSuccessful() {
    final MessageHeaders messageHeaders =
        MessageHeaders.builder().withSubOperation("message_event").build();
    final QueueListenerStub.MessageSample.MessageSampleBuilder message =
        QueueListenerStub.MessageSample.builder().message("hu");
    messagePublisher.publish(Topic.PLACEMENT, message, messageHeaders);

    verify(notificationMessagingTemplate)
        .convertAndSend(
            eq(MESSAGING_PREFIX + Topic.PLACEMENT.getName()),
            eq(message),
            any(org.springframework.messaging.MessageHeaders.class));
    verify(publishSuccessCounterMock).inc();
    verify(publishFailureCounterMock, never()).inc();
    verify(timerMock).time();
    verify(contextMock).close();
  }

  @Test
  void publishMessageFailure() {
    final MessageHeaders messageHeaders =
        MessageHeaders.builder().withSubOperation("message_event").build();
    final QueueListenerStub.MessageSample.MessageSampleBuilder message =
        QueueListenerStub.MessageSample.builder().message("hu");
    doThrow(MessagingException.class)
        .when(notificationMessagingTemplate)
        .convertAndSend(
            eq(MESSAGING_PREFIX + Topic.PLACEMENT.getName()),
            eq(message),
            any(org.springframework.messaging.MessageHeaders.class));

    assertThrows(
        MessagingException.class,
        () -> {
          messagePublisher.publish(Topic.PLACEMENT, message, messageHeaders);
        });

    verify(notificationMessagingTemplate)
        .convertAndSend(
            eq(MESSAGING_PREFIX + Topic.PLACEMENT.getName()),
            eq(message),
            any(org.springframework.messaging.MessageHeaders.class));
    verify(publishSuccessCounterMock, never()).inc();
    verify(publishFailureCounterMock).inc();
    verify(timerMock).time();
    verify(contextMock).close();
  }

  @Test
  void whenTopicIsNull_thenThrowException() {
    assertThrows(NullPointerException.class, () -> messagePublisher.publish(null, "", null));
  }

  @Test
  void whenMessageIsNull_thenThrowExcpetion() {
    assertThrows(
        NullPointerException.class, () -> messagePublisher.publish(Topic.PLACEMENT, null, null));
  }

  @Test
  void whenPublishMessageWithCorrelationId_thenMessagePublishedAndStoredInRepository()
      throws JsonProcessingException {
    final HashOperations hashOperations = mock(HashOperations.class);
    when(redisTemplate.opsForHash()).thenReturn(hashOperations);

    final MessageHeaders messageHeaders =
        MessageHeaders.builder()
            .withCorrelationId(UUID.randomUUID().toString())
            .withSubOperation("message_event")
            .build();
    final QueueListenerStub.MessageSample message =
        QueueListenerStub.MessageSample.builder().message("hu").build();
    messagePublisher.publish(Topic.PLACEMENT, message, messageHeaders);

    verify(notificationMessagingTemplate)
        .convertAndSend(
            eq(MESSAGING_PREFIX + Topic.PLACEMENT.getName()),
            eq(message),
            any(org.springframework.messaging.MessageHeaders.class));
    verify(objectMapper).writeValueAsString(message);
    verify(redisTemplate).opsForHash();
    verify(hashOperations)
        .putIfAbsent(
            eq(messagingSdkProperties.getAdjustedStoreKey()),
            eq(messageHeaders.getCorrelationId()),
            any(MessageContainer.class));
    verify(publishSuccessCounterMock).inc();
    verify(timerMock).time();
    verify(contextMock).close();
  }

  @Test
  void
      whenPublishMessageWithCorrelationIdAndNoRedisTemplate_thenMessagePublishedAndNotStoredInRepository() {
    ReflectionTestUtils.setField(messagePublisher, "redisTemplate", null);
    final MessageHeaders messageHeaders =
        MessageHeaders.builder()
            .withCorrelationId(UUID.randomUUID().toString())
            .withSubOperation("message_event")
            .build();
    final QueueListenerStub.MessageSample message =
        QueueListenerStub.MessageSample.builder().message("hu").build();
    messagePublisher.publish(Topic.PLACEMENT, message, messageHeaders);

    verify(notificationMessagingTemplate)
        .convertAndSend(
            eq(MESSAGING_PREFIX + Topic.PLACEMENT.getName()),
            eq(message),
            any(org.springframework.messaging.MessageHeaders.class));
    verify(publishSuccessCounterMock).inc();
    verify(timerMock).time();
    verify(contextMock).close();
  }

  @Test
  void
      whenPublishMessageWithCorrelationIdAndRedisTemplateFails_thenMessagePublishedAndNotStoredInRepository()
          throws JsonProcessingException {
    when(redisTemplate.opsForHash()).thenThrow(RuntimeException.class);

    final MessageHeaders messageHeaders =
        MessageHeaders.builder()
            .withCorrelationId(UUID.randomUUID().toString())
            .withSubOperation("message_event")
            .build();
    final QueueListenerStub.MessageSample message =
        QueueListenerStub.MessageSample.builder().message("hu").build();
    messagePublisher.publish(Topic.PLACEMENT, message, messageHeaders);

    verify(notificationMessagingTemplate)
        .convertAndSend(
            eq(MESSAGING_PREFIX + Topic.PLACEMENT.getName()),
            eq(message),
            any(org.springframework.messaging.MessageHeaders.class));
    verify(objectMapper).writeValueAsString(message);
    verify(redisTemplate).opsForHash();
    verify(publishSuccessCounterMock).inc();
    verify(timerMock).time();
    verify(contextMock).close();
  }

  @Test
  void
      whenPublishMessageWithCorrelationIdAndObjectMapperFails_thenMessagePublishedAndNotStoredInRepository()
          throws JsonProcessingException {
    when(objectMapper.writeValueAsString(any())).thenThrow(RuntimeException.class);

    final MessageHeaders messageHeaders =
        MessageHeaders.builder()
            .withCorrelationId(UUID.randomUUID().toString())
            .withSubOperation("message_event")
            .build();
    final QueueListenerStub.MessageSample message =
        QueueListenerStub.MessageSample.builder().message("hu").build();
    messagePublisher.publish(Topic.PLACEMENT, message, messageHeaders);

    verify(notificationMessagingTemplate)
        .convertAndSend(
            eq(MESSAGING_PREFIX + Topic.PLACEMENT.getName()),
            eq(message),
            any(org.springframework.messaging.MessageHeaders.class));
    verify(objectMapper).writeValueAsString(message);
    verifyNoMoreInteractions(redisTemplate);
    verify(publishSuccessCounterMock).inc();
    verify(timerMock).time();
    verify(contextMock).close();
  }
}
