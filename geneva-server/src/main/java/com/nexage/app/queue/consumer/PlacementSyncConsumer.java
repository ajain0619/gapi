package com.nexage.app.queue.consumer;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.PositionBuyer;
import com.nexage.admin.core.repository.PositionBuyerRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.app.queue.mapper.EnrichPlacementMessageMapper;
import com.nexage.app.queue.model.EnrichPlacementResultMessage;
import com.ssp.geneva.sdk.messaging.framework.Queue;
import com.ssp.geneva.sdk.messaging.framework.annotation.QueueListener;
import java.util.Map;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PlacementSyncConsumer extends BaseSyncConsumer<EnrichPlacementResultMessage> {

  @Value("${geneva.server.ssp.geneva.queue.placement.syncup.enabled:false}")
  private boolean enableSync;

  @Value("${geneva.server.native.placement.gemini.id}")
  private Long geminiCompanyId;

  private final PositionBuyerRepository positionBuyerRepository;
  private final PositionRepository positionRepository;

  @Autowired
  public PlacementSyncConsumer(
      PositionBuyerRepository positionBuyerRepository, PositionRepository positionRepository) {
    this.positionBuyerRepository = positionBuyerRepository;
    this.positionRepository = positionRepository;
  }

  /**
   * process EnrichPlacementResultMessage from the queue
   *
   * @param message message parsed from the queue
   * @param headers message's headers
   */
  @QueueListener(value = Queue.PLACEMENT_SSP, policy = SqsMessageDeletionPolicy.ON_SUCCESS)
  public void processMessage(
      @Payload EnrichPlacementResultMessage message, @Headers Map<String, Object> headers) {
    doProcess(message, headers);
  }

  protected void process(
      @Valid EnrichPlacementResultMessage placementMessage, Map<String, Object> headers) {
    PositionBuyer positionBuyer =
        positionBuyerRepository
            .findByPositionPid(Long.parseLong(placementMessage.getPlacementPid()))
            .orElse(null);

    if (positionBuyer != null) {
      log.info("updating existing {}", positionBuyer);
      positionBuyer.setBuyerPositionId(placementMessage.getSectionPid());
      positionBuyerRepository.save(positionBuyer);
    } else {
      log.info("creating new PositionBuyer for PositionId {}", placementMessage.getPlacementPid());
      Position position =
          positionRepository
              .findById(Long.parseLong(placementMessage.getPlacementPid()))
              .orElse(null);
      if (position != null) {
        bindBuyerFromMessage(position, placementMessage);
        positionRepository.save(position);
      } else {
        log.error("couldn't find Position with Id {}", placementMessage.getPlacementPid());
      }
    }
  }

  private void bindBuyerFromMessage(Position position, EnrichPlacementResultMessage message) {
    var positionBuyer = EnrichPlacementMessageMapper.MAPPER.map(message);
    positionBuyer.setPosition(position);
    positionBuyer.setCompanyPid(geminiCompanyId);
    position.setPositionBuyer(positionBuyer);
  }

  @Override
  protected boolean isSyncEnabled() {
    return enableSync;
  }

  @Override
  Logger getLogger() {
    return log;
  }
}
