package com.ssp.geneva.sdk.messaging.stub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssp.geneva.sdk.messaging.framework.annotation.QueueListener;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

@Setter
public class FilteredQueueListenerStub {

  private Consumer<CreateMessage> createMessageCallback = t -> {};
  private Consumer<Map<String, Object>> createHeadersCallback = t -> {};

  private Consumer<UpdateMessage> updateMessageCallback = t -> {};
  private Consumer<Map<String, Object>> updateHeadersCallback = t -> {};

  @QueueListener(value = "filtered_queue", filter = "h-subOperation=create")
  public void onCreateMessage(
      @Payload CreateMessage message, @Headers Map<String, Object> headers) {
    createMessageCallback.accept(message);
    createHeadersCallback.accept(headers);
  }

  @QueueListener(value = "filtered_queue", filter = "h-subOperation=update")
  public void onUpdateMessage(
      @Payload UpdateMessage message, @Headers Map<String, Object> headers) {
    updateMessageCallback.accept(message);
    updateHeadersCallback.accept(headers);
  }

  @Data
  @Builder
  public static class CreateMessage {
    private String message;

    @JsonCreator
    public CreateMessage(@JsonProperty("message") String message) {
      this.message = message;
    }
  }

  @Data
  @Builder
  public static class UpdateMessage {
    private String message;
    private Date createDate;

    @JsonCreator
    public UpdateMessage(
        @JsonProperty("message") String message, @JsonProperty("createDate") Date date) {
      this.message = message;
      this.createDate = date;
    }
  }
}
