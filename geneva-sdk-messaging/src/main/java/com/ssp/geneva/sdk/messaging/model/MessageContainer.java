package com.ssp.geneva.sdk.messaging.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@Builder
public class MessageContainer implements Serializable {

  private final String topic;

  private final Map<String, Object> headers;

  private final String message;

  private final Instant timestamp;

  private final Integer retry;

  @JsonCreator
  public MessageContainer(
      @JsonProperty("topic") String topic,
      @JsonProperty("headers") Map<String, Object> headers,
      @JsonProperty("message") String message,
      @JsonProperty("timestamp") Instant timestamp,
      @JsonProperty("retry") Integer retry) {
    this.topic = topic;
    this.headers = headers;
    this.message = message;
    this.timestamp = timestamp;
    this.retry = retry;
  }
}
