package com.ssp.geneva.sdk.messaging.framework;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkMetrics;
import com.ssp.geneva.sdk.messaging.config.MessagingSdkProperties;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import com.ssp.geneva.sdk.messaging.stub.FilteredQueueListenerStub;
import com.ssp.geneva.sdk.messaging.stub.QueueListenerStub;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.MessageBuilder;

@ExtendWith(MockitoExtension.class)
class MessagingSdkQueueMessageHandlerTest {

  @Spy
  private MessagingSdkProperties messagingSdkProperties =
      MessagingSdkProperties.builder()
          .messagingPrefix("geneva-messaging-dev")
          .storeKey("messagesStore")
          .build();

  @Mock private List<MessageConverter> messageConverters;

  @Mock private RedisTemplate redisTemplate;

  @Mock private MessagingSdkMetrics messagingSdkMetrics;

  @InjectMocks private MessagingSdkQueueMessageHandler messagingSdkQueueMessageHandler;

  private final Counter exceptionThrownCounterMock = mock(Counter.class);
  private final Timer messageHandledTimerMock = mock(Timer.class);
  private final Timer.Context contextMock = mock(Timer.Context.class);
  private final Counter messagesHandledCounterMock = mock(Counter.class);
  private final Counter messagesNotMatchedCounterMock = mock(Counter.class);

  @BeforeEach
  void setUp() throws Exception {
    lenient()
        .when(messagingSdkMetrics.getExceptionThrownCounter())
        .thenReturn(exceptionThrownCounterMock);
    lenient()
        .when(messagingSdkMetrics.getMessageHandledTimer())
        .thenReturn(messageHandledTimerMock);
    lenient().when(messageHandledTimerMock.time()).thenReturn(contextMock);
    lenient()
        .when(messagingSdkMetrics.getMessagesHandledCounter())
        .thenReturn(messagesHandledCounterMock);
    lenient()
        .when(messagingSdkMetrics.getMessagesNotMatchedCounter())
        .thenReturn(messagesNotMatchedCounterMock);
  }

  @Test
  void testGetMappingForMethodSuccessfully()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    final QueueListenerStub queueListenerStub = new QueueListenerStub();
    final Object mapping =
        messagingSdkQueueMessageHandler.getMappingForMethod(
            queueListenerStub
                .getClass()
                .getDeclaredMethod(
                    "onMessageReceived", QueueListenerStub.MessageSample.class, Map.class),
            queueListenerStub.getClass());

    final Method getLogicalResourceIds = mapping.getClass().getMethod("getLogicalResourceIds");
    final Object logicalResourceIds = getLogicalResourceIds.invoke(mapping);
    assertTrue(Set.class.isInstance(logicalResourceIds));
    assertThat(
        ((Set<String>) logicalResourceIds),
        hasItem(messagingSdkProperties.getMessagingPrefix() + "-queue"));
    final Method getDeletionPolicy = mapping.getClass().getMethod("getDeletionPolicy");
    final Object deletionPolicy = getDeletionPolicy.invoke(mapping);
    assertTrue(SqsMessageDeletionPolicy.class.isInstance(deletionPolicy));
    assertThat(deletionPolicy, is(SqsMessageDeletionPolicy.ALWAYS));
  }

  @Test
  void testGetMappingForMethodFailed() throws NoSuchMethodException {
    final QueueListenerStub queueListenerStub = new QueueListenerStub();
    final Object mapping =
        messagingSdkQueueMessageHandler.getMappingForMethod(
            queueListenerStub.getClass().getDeclaredMethod("setHeadersCallback", Consumer.class),
            queueListenerStub.getClass());
    assertNull(mapping);
  }

  @Test
  void whenQueueListenerWithFilter_theMappingIsSetCorrectly()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    final Object onCreateMessage =
        messagingSdkQueueMessageHandler.getMappingForMethod(
            FilteredQueueListenerStub.class.getMethod(
                "onCreateMessage", FilteredQueueListenerStub.CreateMessage.class, Map.class),
            FilteredQueueListenerStub.class);

    final Method getLogicalResourceIds =
        onCreateMessage.getClass().getMethod("getLogicalResourceIds");
    final Object logicalResourceIds = getLogicalResourceIds.invoke(onCreateMessage);
    assertTrue(Set.class.isInstance(logicalResourceIds));
    assertThat(
        ((Set<String>) logicalResourceIds),
        hasItem(messagingSdkProperties.getMessagingPrefix() + "-filtered_queue"));
    final Method getDeletionPolicy = onCreateMessage.getClass().getMethod("getDeletionPolicy");
    final Object deletionPolicy = getDeletionPolicy.invoke(onCreateMessage);
    assertTrue(SqsMessageDeletionPolicy.class.isInstance(deletionPolicy));
    assertThat(deletionPolicy, is(SqsMessageDeletionPolicy.ALWAYS));
    final Method getFilters = onCreateMessage.getClass().getDeclaredMethod("getFilters");
    final Object filters = getFilters.invoke(onCreateMessage);
    assertTrue(Map.class.isInstance(filters));
    assertThat(((Map<String, String>) filters), hasEntry("h-subOperation", "create"));
  }

  @Test
  void whenMappingIsMatched_thenReturnMapping() throws NoSuchMethodException {
    Map<String, Object> messageHeaders = new HashMap<>();
    messageHeaders.put(MessageHeaders.SUB_OPERATION_HEADER, "create");
    messageHeaders.put(
        "LogicalResourceId", messagingSdkProperties.getMessagingPrefix() + "-filtered_queue");
    final Message<FilteredQueueListenerStub.CreateMessage> message =
        MessageBuilder.createMessage(
            FilteredQueueListenerStub.CreateMessage.builder().build(),
            new org.springframework.messaging.MessageHeaders(messageHeaders));
    final Object matchingMapping =
        messagingSdkQueueMessageHandler.getMatchingMapping(
            messagingSdkQueueMessageHandler.getMappingForMethod(
                FilteredQueueListenerStub.class.getMethod(
                    "onCreateMessage", FilteredQueueListenerStub.CreateMessage.class, Map.class),
                FilteredQueueListenerStub.class),
            message);
    assertNotNull(matchingMapping);
  }

  @Test
  void whenMappingIsNotMatched_thenReturnNull() throws NoSuchMethodException {
    Map<String, Object> messageHeaders = new HashMap<>();
    messageHeaders.put("LogicalResourceId", "dev_filtered_queue");
    final Message<FilteredQueueListenerStub.CreateMessage> message =
        MessageBuilder.createMessage(
            FilteredQueueListenerStub.CreateMessage.builder().build(),
            new org.springframework.messaging.MessageHeaders(messageHeaders));
    final Object matchingMapping =
        messagingSdkQueueMessageHandler.getMatchingMapping(
            messagingSdkQueueMessageHandler.getMappingForMethod(
                FilteredQueueListenerStub.class.getMethod(
                    "onCreateMessage", FilteredQueueListenerStub.CreateMessage.class, Map.class),
                FilteredQueueListenerStub.class),
            message);
    assertNull(matchingMapping);
  }

  @Test
  void whenMessageHasCorrelationId_thenRemoveFromRepository() {
    final HashOperations hashOperations = mock(HashOperations.class);
    when(redisTemplate.opsForHash()).thenReturn(hashOperations);

    Map<String, Object> messageHeaders = new HashMap<>();
    String replyCorrelationId = UUID.randomUUID().toString();
    messageHeaders.put(MessageHeaders.REPLY_CORRELATION_ID_HEADER, replyCorrelationId);
    final Message<FilteredQueueListenerStub.CreateMessage> message =
        MessageBuilder.createMessage(
            FilteredQueueListenerStub.CreateMessage.builder().build(),
            new org.springframework.messaging.MessageHeaders(messageHeaders));
    messagingSdkQueueMessageHandler.handleMessageInternal(message, "");

    verify(redisTemplate).opsForHash();
    verify(hashOperations).delete(messagingSdkProperties.getAdjustedStoreKey(), replyCorrelationId);
    verify(messagesNotMatchedCounterMock).inc();
  }

  @Test
  void whenMessageWithoutCorrelationId_thenRepositoryIsNotTouched() {
    Map<String, Object> messageHeaders = new HashMap<>();
    final Message<FilteredQueueListenerStub.CreateMessage> message =
        MessageBuilder.createMessage(
            FilteredQueueListenerStub.CreateMessage.builder().build(),
            new org.springframework.messaging.MessageHeaders(messageHeaders));
    messagingSdkQueueMessageHandler.handleMessageInternal(message, "");

    verify(messagesNotMatchedCounterMock).inc();
    verifyNoInteractions(redisTemplate);
  }

  @Test
  void whenRedisTemplateThrowsException_thenNoExceptionIsThrown() {
    when(redisTemplate.opsForHash()).thenThrow(RuntimeException.class);

    Map<String, Object> messageHeaders = new HashMap<>();
    String replyCorrelationId = UUID.randomUUID().toString();
    messageHeaders.put(MessageHeaders.REPLY_CORRELATION_ID_HEADER, replyCorrelationId);
    final Message<FilteredQueueListenerStub.CreateMessage> message =
        MessageBuilder.createMessage(
            FilteredQueueListenerStub.CreateMessage.builder().build(),
            new org.springframework.messaging.MessageHeaders(messageHeaders));
    messagingSdkQueueMessageHandler.handleMessageInternal(message, "");

    verify(messagesNotMatchedCounterMock).inc();
    verify(redisTemplate).opsForHash();
  }
}
