package com.ssp.geneva.sdk.messaging.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@Getter
@Builder(setterPrefix = "with")
@ToString
public class MessageHeaders {

  public static final String CORRELATION_ID_HEADER = "h-correlationId";
  public static final String REPLY_CORRELATION_ID_HEADER = "h-replyCorrelationId";
  public static final String OPERATION_HEADER = "h-operation";
  public static final String SUB_OPERATION_HEADER = "h-subOperation";
  public static final String SOURCE_HEADER = "h-source";
  public static final String ENTITY_ID_HEADER = "h-entityId";
  public static final String MESSAGING_TRACE_HEADER = "h-messagingTraceId";
  public static final String USERNAME_HEADER = "h-userName";

  @Builder.Default private String contentType = "application/json";
  private String replyChannel;
  private String errorChannel;
  private String correlationId;
  private String replyCorrelationId;
  private String operation;
  private String subOperation;
  private String source;
  private Long entityId;
  private String messagingTraceId;
  private String userName;

  private Map<String, Object> customHeaders;

  public Map<String, Object> toMap() {
    Map<String, Object> headers = new HashMap<>();
    if (StringUtils.isNotEmpty(contentType)) {
      headers.put(org.springframework.messaging.MessageHeaders.CONTENT_TYPE, contentType);
    }
    if (StringUtils.isNotEmpty(replyChannel)) {
      headers.put(org.springframework.messaging.MessageHeaders.REPLY_CHANNEL, replyChannel);
    }
    if (StringUtils.isNotEmpty(errorChannel)) {
      headers.put(org.springframework.messaging.MessageHeaders.ERROR_CHANNEL, errorChannel);
    }
    if (StringUtils.isNotEmpty(correlationId)) {
      headers.put(CORRELATION_ID_HEADER, correlationId);
    }
    if (StringUtils.isNotEmpty(replyCorrelationId)) {
      headers.put(REPLY_CORRELATION_ID_HEADER, replyCorrelationId);
    }
    if (StringUtils.isNotEmpty(operation)) {
      headers.put(OPERATION_HEADER, operation);
    }
    if (StringUtils.isNotEmpty(subOperation)) {
      headers.put(SUB_OPERATION_HEADER, subOperation);
    }
    if (StringUtils.isNotEmpty(source)) {
      headers.put(SOURCE_HEADER, source);
    }
    if (entityId != null) {
      headers.put(ENTITY_ID_HEADER, entityId);
    }
    if (messagingTraceId != null) {
      headers.put(MESSAGING_TRACE_HEADER, messagingTraceId);
    }
    if (userName != null) {
      headers.put(USERNAME_HEADER, userName);
    }
    if (customHeaders != null) {
      headers.putAll(customHeaders);
    }

    return headers;
  }
}
