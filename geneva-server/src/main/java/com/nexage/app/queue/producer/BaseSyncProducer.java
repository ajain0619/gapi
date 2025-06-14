package com.nexage.app.queue.producer;

import static com.nexage.app.queue.model.MessageHeadersConst.MESSAGING_TRACE_ID_MDC;
import static com.nexage.app.queue.model.MessageHeadersConst.USERNAME_MDC;

import com.nexage.app.queue.model.SyncMessage;
import com.nexage.app.queue.model.event.SyncEvent;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import com.ssp.geneva.sdk.messaging.model.Topic;
import com.ssp.geneva.sdk.messaging.service.MessagePublisher;
import java.util.Optional;
import java.util.UUID;
import org.apache.log4j.MDC;
import org.apache.logging.log4j.Logger;

public abstract class BaseSyncProducer<
    ENTITY, EVENT extends SyncEvent, MESSAGE extends SyncMessage> {

  private MessagePublisher messagePublisher;

  protected BaseSyncProducer(MessagePublisher messagePublisher) {
    this.messagePublisher = messagePublisher;
  }

  public abstract void publishEvent(EVENT event);

  protected void publish(ENTITY entity) {
    if (isEnableSync()) {
      if (isValid(entity)) {
        MessageHeaders headers = populateAndGetMessageHeaders(entity);
        MESSAGE message = getMessage(entity);
        Topic topic = getTopic();
        try {
          MDC.put(MESSAGING_TRACE_ID_MDC, headers.getMessagingTraceId());
          getLogger().info("publishing msg:{} headers:{} to:{}", message, headers, topic);
          messagePublisher.publish(topic, message, headers);
        } finally {
          MDC.remove(MESSAGING_TRACE_ID_MDC);
        }
      } else {
        getLogger().error("invalid {}", entity);
      }
    } else {
      getLogger().warn("sync is disabled for {} ", entity);
    }
  }

  /**
   * adding messagingTraceId, and optionally username to MessageHeaders
   *
   * @param entity
   * @return
   */
  private MessageHeaders populateAndGetMessageHeaders(ENTITY entity) {
    MessageHeaders.MessageHeadersBuilder headersBuilder = getHeadersBuilder(entity);
    headersBuilder.withMessagingTraceId(UUID.randomUUID().toString());
    Optional.ofNullable(MDC.get(USERNAME_MDC))
        .map(String::valueOf)
        .ifPresent(headersBuilder::withUserName);
    return headersBuilder.build();
  }

  protected boolean isEnableSync() {
    return false;
  }

  protected boolean isValid(ENTITY entity) {
    return false;
  }

  protected abstract Logger getLogger();

  protected abstract Topic getTopic();

  protected abstract MESSAGE getMessage(ENTITY entity);

  protected abstract MessageHeaders.MessageHeadersBuilder getHeadersBuilder(ENTITY entity);
}
