package com.ssp.geneva.sdk.messaging.stub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssp.geneva.sdk.messaging.framework.annotation.QueueListener;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

@Setter
public class QueueListenerStub {

  private Consumer<MessageSample> messageCallback = t -> {};
  private Consumer<Map<String, Object>> headersCallback = t -> {};

  @QueueListener(value = "queue")
  public void onMessageReceived(
      @Payload MessageSample messageSample, @Headers Map<String, Object> headers) {
    messageCallback.accept(messageSample);
    headersCallback.accept(headers);
  }

  @Data
  @Builder
  public static class MessageSample {
    private String message;

    @JsonCreator
    public MessageSample(@JsonProperty("message") String message) {
      this.message = message;
    }
  }
}
