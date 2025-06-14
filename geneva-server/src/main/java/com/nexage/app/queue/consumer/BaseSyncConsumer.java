package com.nexage.app.queue.consumer;

import static com.nexage.app.queue.model.MessageHeadersConst.MESSAGING_TRACE_ID_MDC;
import static com.nexage.app.queue.model.MessageHeadersConst.USERNAME_MDC;

import com.nexage.app.queue.model.SyncMessage;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import java.util.Map;
import java.util.Optional;
import org.apache.log4j.MDC;
import org.apache.logging.log4j.Logger;

public abstract class BaseSyncConsumer<T extends SyncMessage> {

  abstract void processMessage(T message, Map<String, Object> headers);

  protected void doProcess(T message, Map<String, Object> headers) {
    if (isSyncEnabled()) {
      try {
        addMdcField(headers, MessageHeaders.MESSAGING_TRACE_HEADER, MESSAGING_TRACE_ID_MDC);
        addMdcField(headers, MessageHeaders.USERNAME_HEADER, USERNAME_MDC);
        getLogger().info("publishing msg:{} headers:{}", message, headers);
        process(message, headers);
      } finally {
        MDC.remove(MESSAGING_TRACE_ID_MDC);
        MDC.remove(USERNAME_MDC);
      }
    } else {
      getLogger().warn("sync is disabled for {}", message);
    }
  }

  protected boolean isSyncEnabled() {
    return false;
  }

  abstract Logger getLogger();

  abstract void process(T message, Map<String, Object> headers);

  private void addMdcField(Map<String, Object> headers, String headerName, String mdcField) {
    Optional.ofNullable(headers.get(headerName))
        .map(String::valueOf)
        .ifPresent(requestId -> MDC.put(mdcField, requestId));
  }
}
