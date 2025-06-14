package com.nexage.app.queue.producer;

import com.nexage.admin.core.model.Position;
import com.nexage.app.queue.mapper.EnrichPlacementMessageMapper;
import com.nexage.app.queue.model.EnrichPlacementCommandMessage;
import com.nexage.app.queue.model.MessageHeadersConst;
import com.nexage.app.queue.model.event.SyncEvent;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import com.ssp.geneva.sdk.messaging.model.Topic;
import com.ssp.geneva.sdk.messaging.service.MessagePublisher;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PlacementSyncProducer
    extends BaseSyncProducer<Position, SyncEvent<Position>, EnrichPlacementCommandMessage> {

  @Value("${geneva.server.ssp.geneva.queue.placement.syncup.enabled:false}")
  private boolean enableSync;

  @Autowired
  public PlacementSyncProducer(MessagePublisher messagePublisher) {
    super(messagePublisher);
  }

  public void publishEvent(SyncEvent<Position> event) {
    try {
      publish(event.getData());
    } catch (Exception e) {
      log.error("error while publishing event {}", event, e);
    }
  }

  @Override
  protected boolean isEnableSync() {
    return enableSync;
  }

  @Override
  protected boolean isValid(Position position) {
    return position.getSite() != null
        && position.getSite().getPid() != null
        && position.getPid() != null;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }

  @Override
  protected Topic getTopic() {
    return Topic.PLACEMENT;
  }

  @Override
  protected EnrichPlacementCommandMessage getMessage(Position position) {
    return EnrichPlacementMessageMapper.MAPPER.map(position);
  }

  @Override
  protected MessageHeaders.MessageHeadersBuilder getHeadersBuilder(Position position) {
    return MessageHeaders.builder()
        .withCorrelationId(UUID.randomUUID().toString())
        .withSubOperation(MessageHeadersConst.ENRICH_MESSAGE_COMMAND)
        .withOperation(MessageHeadersConst.Operation.CREATE)
        .withSource(MessageHeadersConst.SSP_SOURCE)
        .withEntityId(position.getPid());
  }
}
