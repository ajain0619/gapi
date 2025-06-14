package com.nexage.app.queue.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.app.queue.mapper.CompanyEventMapper;
import com.nexage.app.queue.model.MessageHeadersConst;
import com.nexage.app.queue.model.event.SyncEvent;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import com.ssp.geneva.sdk.messaging.model.Topic;
import com.ssp.geneva.sdk.messaging.service.MessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CompanySyncProducerTest {

  private static final Long COMPANY_PID = 11L;
  private static final String name = "company name";

  @Mock private Company company;

  @Mock private MessagePublisher messagePublisher;

  @InjectMocks private CompanySyncProducer subject;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(subject, "enableSync", true);
  }

  @Test
  void testPublishMessage() {
    when(company.getPid()).thenReturn(COMPANY_PID);
    when(company.getName()).thenReturn(name);

    subject.publishEvent(SyncEvent.createOf(company));
    var companyEventMessage = CompanyEventMapper.MAPPER.map(company);

    ArgumentCaptor<MessageHeaders> someArgumentCaptor =
        ArgumentCaptor.forClass(MessageHeaders.class);
    verify(messagePublisher)
        .publish(eq(Topic.COMPANY), eq(companyEventMessage), someArgumentCaptor.capture());

    MessageHeaders headers = someArgumentCaptor.getValue();
    var messageHeaders = headers.toMap();

    assertEquals(
        MessageHeadersConst.CREATE_COMPANY_EVENT,
        messageHeaders.get(MessageHeaders.SUB_OPERATION_HEADER));
    assertEquals(
        MessageHeadersConst.Operation.CREATE, messageHeaders.get(MessageHeaders.OPERATION_HEADER));
    assertEquals(MessageHeadersConst.SSP_SOURCE, messageHeaders.get(MessageHeaders.SOURCE_HEADER));
    assertEquals(messageHeaders.get(MessageHeaders.ENTITY_ID_HEADER), company.getPid());
    assertNull(messageHeaders.get(MessageHeaders.CORRELATION_ID_HEADER));
    assertNotNull(messageHeaders.get(MessageHeaders.MESSAGING_TRACE_HEADER));
  }

  @Test
  void testPublishInvalidPosition() {
    when(company.getPid()).thenReturn(null);
    subject.publishEvent(SyncEvent.createOf(company));

    verify(messagePublisher, never()).publish(any(), any(), any());

    when(company.getName()).thenReturn("g");
    when(company.getPid()).thenReturn(null);

    subject.publishEvent(SyncEvent.createOf(company));

    verify(messagePublisher, never()).publish(any(), any(), any());

    when(company.getPid()).thenReturn(COMPANY_PID);
    when(company.getName()).thenReturn(null);

    subject.publishEvent(SyncEvent.createOf(company));

    verify(messagePublisher, never()).publish(any(), any(), any());

    when(company.getPid()).thenReturn(COMPANY_PID);
    when(company.getName()).thenReturn("");

    subject.publishEvent(SyncEvent.createOf(company));

    verify(messagePublisher, never()).publish(any(), any(), any());
  }

  @Test
  void testPublishNoOp() {
    ReflectionTestUtils.setField(subject, "enableSync", false);

    subject.publishEvent(SyncEvent.createOf(company));

    verify(messagePublisher, never()).publish(any(), any(), any());
  }

  @Test
  void testPublishWithException() {
    ReflectionTestUtils.setField(subject, "enableSync", true);

    when(company.getPid()).thenReturn(COMPANY_PID);
    when(company.getName()).thenReturn(name);

    doThrow(new RuntimeException()).when(messagePublisher).publish(any(), any(), any());
    subject.publishEvent(SyncEvent.createOf(company));
    var companyEventMessage = CompanyEventMapper.MAPPER.map(company);
    assertNotNull(companyEventMessage);
  }
}
