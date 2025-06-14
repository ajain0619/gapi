package com.nexage.app.queue.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.queue.mapper.EnrichPlacementMessageMapper;
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
class PlacementSyncProducerTest {

  private static final Long POSITION_PID = 11L;
  private static final Long SITE_PID = 10L;

  @Mock private Position position;

  @Mock private Site site;

  @Mock private MessagePublisher messagePublisher;

  @InjectMocks private PlacementSyncProducer subject;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(subject, "enableSync", true);
  }

  @Test
  void testPublishMessage() {
    when(position.getPid()).thenReturn(POSITION_PID);
    when(position.getSite()).thenReturn(site);
    when(site.getPid()).thenReturn(SITE_PID);

    subject.publishEvent(SyncEvent.createOf(position));
    var enrichPlacementCommandMessage = EnrichPlacementMessageMapper.MAPPER.map(position);

    ArgumentCaptor<MessageHeaders> someArgumentCaptor =
        ArgumentCaptor.forClass(MessageHeaders.class);
    verify(messagePublisher)
        .publish(
            eq(Topic.PLACEMENT), eq(enrichPlacementCommandMessage), someArgumentCaptor.capture());

    MessageHeaders headers = someArgumentCaptor.getValue();
    var messageHeaders = headers.toMap();

    assertEquals(
        MessageHeadersConst.ENRICH_MESSAGE_COMMAND,
        messageHeaders.get(MessageHeaders.SUB_OPERATION_HEADER));
    assertEquals(
        MessageHeadersConst.Operation.CREATE, messageHeaders.get(MessageHeaders.OPERATION_HEADER));
    assertNotNull(messageHeaders.get(MessageHeaders.CORRELATION_ID_HEADER));
    assertNotNull(messageHeaders.get(MessageHeaders.MESSAGING_TRACE_HEADER));
    assertEquals(MessageHeadersConst.SSP_SOURCE, messageHeaders.get(MessageHeaders.SOURCE_HEADER));
    assertEquals(messageHeaders.get(MessageHeaders.ENTITY_ID_HEADER), position.getPid());
  }

  @Test
  void testPublishInvalidPosition() {
    when(position.getSite()).thenReturn(null);
    subject.publishEvent(SyncEvent.createOf(position));

    verify(messagePublisher, never()).publish(any(), any(), any());

    when(position.getSite()).thenReturn(site);
    when(site.getPid()).thenReturn(null);

    subject.publishEvent(SyncEvent.createOf(position));

    verify(messagePublisher, never()).publish(any(), any(), any());

    when(position.getSite()).thenReturn(site);
    when(site.getPid()).thenReturn(SITE_PID);
    when(position.getPid()).thenReturn(null);

    subject.publishEvent(SyncEvent.createOf(position));

    verify(messagePublisher, never()).publish(any(), any(), any());
  }

  @Test
  void testPublishNoOp() {
    ReflectionTestUtils.setField(subject, "enableSync", false);

    subject.publishEvent(SyncEvent.createOf(position));

    verify(messagePublisher, never()).publish(any(), any(), any());
  }

  @Test
  void testPublishWithException() {
    ReflectionTestUtils.setField(subject, "enableSync", true);

    when(position.getPid()).thenReturn(POSITION_PID);
    when(position.getSite()).thenReturn(site);
    when(site.getPid()).thenReturn(SITE_PID);

    doThrow(new RuntimeException()).when(messagePublisher).publish(any(), any(), any());
    subject.publishEvent(SyncEvent.createOf(position));
    var enrichPlacementCommandMessage = EnrichPlacementMessageMapper.MAPPER.map(position);
    assertNotNull(enrichPlacementCommandMessage);
  }
}
