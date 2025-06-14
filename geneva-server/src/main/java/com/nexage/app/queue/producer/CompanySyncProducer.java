package com.nexage.app.queue.producer;

import com.nexage.admin.core.model.Company;
import com.nexage.app.queue.mapper.CompanyEventMapper;
import com.nexage.app.queue.model.CompanyEventMessage;
import com.nexage.app.queue.model.MessageHeadersConst;
import com.nexage.app.queue.model.event.SyncEvent;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import com.ssp.geneva.sdk.messaging.model.Topic;
import com.ssp.geneva.sdk.messaging.service.MessagePublisher;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Log4j2
@Component
public class CompanySyncProducer
    extends BaseSyncProducer<Company, SyncEvent<Company>, CompanyEventMessage> {

  @Value("${geneva.server.ssp.geneva.queue.company.syncup.enabled:false}")
  private boolean enableSync;

  @Autowired
  public CompanySyncProducer(MessagePublisher messagePublisher) {
    super(messagePublisher);
  }

  public void publishEvent(SyncEvent<Company> event) {
    try {
      publish(event.getData());
    } catch (Exception e) {
      log.info("error while publishing event {}", event, e);
    }
  }

  @Override
  protected boolean isEnableSync() {
    return enableSync;
  }

  @Override
  protected boolean isValid(Company company) {
    return company.getPid() != null && !StringUtils.isEmpty(company.getName());
  }

  @Override
  protected Logger getLogger() {
    return log;
  }

  @Override
  protected Topic getTopic() {
    return Topic.COMPANY;
  }

  @Override
  protected CompanyEventMessage getMessage(Company company) {
    return CompanyEventMapper.MAPPER.map(company);
  }

  @Override
  protected MessageHeaders.MessageHeadersBuilder getHeadersBuilder(Company company) {
    return MessageHeaders.builder()
        .withSubOperation(MessageHeadersConst.CREATE_COMPANY_EVENT)
        .withOperation(MessageHeadersConst.Operation.CREATE)
        .withSource(MessageHeadersConst.SSP_SOURCE)
        .withEntityId(company.getPid());
  }
}
