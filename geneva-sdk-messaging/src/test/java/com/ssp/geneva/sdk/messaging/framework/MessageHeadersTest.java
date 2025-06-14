package com.ssp.geneva.sdk.messaging.framework;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import java.util.Map;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.Test;

class MessageHeadersTest {

  private final String correlationId = "correlationId";
  private final String contentType = "contentType";
  private final String errorChannel = "errorChannel";
  private final String operation = "operation";
  private final String replyChannel = "replyChannel";
  private final String replyCorrelationId = "replyCorrelationId";
  private final String source = "source";
  private final String subOperation = "subOperation";
  private final Map<String, Object> customHeaders = Map.of("Hello", "World");

  @Test
  void testBuilder() {
    final MessageHeaders messageHeaders =
        MessageHeaders.builder()
            .withCorrelationId(correlationId)
            .withContentType(contentType)
            .withCustomHeaders(customHeaders)
            .withErrorChannel(errorChannel)
            .withOperation(operation)
            .withReplyChannel(replyChannel)
            .withReplyCorrelationId(replyCorrelationId)
            .withSource(source)
            .withSubOperation(subOperation)
            .build();

    assertThat(
        messageHeaders.toMap(),
        IsMapContaining.hasEntry(MessageHeaders.CORRELATION_ID_HEADER, correlationId));
    assertThat(
        messageHeaders.toMap(),
        IsMapContaining.hasEntry(
            org.springframework.messaging.MessageHeaders.CONTENT_TYPE, contentType));
    assertThat(
        messageHeaders.toMap(), IsMapContaining.hasEntry("Hello", customHeaders.get("Hello")));
    assertThat(
        messageHeaders.toMap(),
        IsMapContaining.hasEntry(
            org.springframework.messaging.MessageHeaders.ERROR_CHANNEL, errorChannel));
    assertThat(
        messageHeaders.toMap(),
        IsMapContaining.hasEntry(MessageHeaders.OPERATION_HEADER, operation));
    assertThat(
        messageHeaders.toMap(),
        IsMapContaining.hasEntry(
            org.springframework.messaging.MessageHeaders.REPLY_CHANNEL, replyChannel));
    assertThat(
        messageHeaders.toMap(),
        IsMapContaining.hasEntry(MessageHeaders.REPLY_CORRELATION_ID_HEADER, replyCorrelationId));
    assertThat(
        messageHeaders.toMap(), IsMapContaining.hasEntry(MessageHeaders.SOURCE_HEADER, source));
    assertThat(
        messageHeaders.toMap(),
        IsMapContaining.hasEntry(MessageHeaders.SUB_OPERATION_HEADER, subOperation));
  }

  @Test
  void testToMapWhenEmpty() {
    final MessageHeaders messageHeaders =
        MessageHeaders.builder()
            // pick 2 random fields (should behave similar to not setting those fields)
            .withCorrelationId(null)
            .withMessagingTraceId(null)
            .build();

    assertThat(messageHeaders.toMap().size(), is(1));
    assertThat(
        messageHeaders.toMap().get(org.springframework.messaging.MessageHeaders.CONTENT_TYPE),
        is("application/json")); // default field
  }

  @Test
  void testBuilderDefault() {
    final MessageHeaders messageHeaders = MessageHeaders.builder().build();
    assertThat(
        messageHeaders.toMap(),
        IsMapContaining.hasEntry(
            org.springframework.messaging.MessageHeaders.CONTENT_TYPE, "application/json"));
  }

  @Test
  void validateToString() {
    MessageHeaders messageHeaders =
        MessageHeaders.builder().withCorrelationId(correlationId).build();
    assertThat(
        messageHeaders.toString(),
        is(
            "MessageHeaders(contentType=application/json, replyChannel=null, errorChannel=null, correlationId=correlationId, replyCorrelationId=null, operation=null, subOperation=null, source=null, entityId=null, messagingTraceId=null, userName=null, customHeaders=null)"));
  }
}
