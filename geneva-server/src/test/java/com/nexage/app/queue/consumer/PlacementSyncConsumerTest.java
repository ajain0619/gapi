package com.nexage.app.queue.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.PositionBuyer;
import com.nexage.admin.core.repository.PositionBuyerRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.app.queue.model.EnrichPlacementResultMessage;
import com.ssp.geneva.sdk.messaging.model.MessageHeaders;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PlacementSyncConsumerTest {

  private static final Long POSITION_ID = 10L;
  private static final Long COMPANY_ID = 11L;
  private static final Long SITE_ID = 12L;
  private static final String SECTION_ID = "sectionId";
  private static final Long GEMINI_COMPANY_ID = 107L;

  @Mock private PositionBuyerRepository positionBuyerRepository;

  @Mock private PositionRepository positionRepository;

  @InjectMocks private PlacementSyncConsumer subject;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(subject, "enableSync", true);
    ReflectionTestUtils.setField(subject, "geminiCompanyId", GEMINI_COMPANY_ID);
  }

  @Test
  void testProcessEnrichPlacementResultMessagePositionBuyerExists() {
    var msg = new EnrichPlacementResultMessage();
    msg.setPlacementPid(POSITION_ID.toString());
    msg.setCompanyPid(COMPANY_ID.toString());
    msg.setSitePid(SITE_ID.toString());
    msg.setSectionPid(SECTION_ID);

    Map<String, Object> headers =
        Map.of(
            "headerKey1",
            "headervalue1",
            MessageHeaders.CORRELATION_ID_HEADER,
            UUID.randomUUID().toString());

    var positionBuyer = new PositionBuyer();
    positionBuyer.setCompanyPid(COMPANY_ID);
    positionBuyer.setPositionPid(POSITION_ID);
    positionBuyer.setBuyerPositionId("");

    when(positionBuyerRepository.findByPositionPid(POSITION_ID))
        .thenReturn(Optional.of(positionBuyer));

    subject.processMessage(msg, headers);
    positionBuyer.setBuyerPositionId(SECTION_ID);

    verify(positionBuyerRepository).save(positionBuyer);
    verifyNoInteractions(positionRepository);
  }

  @Test
  void testProcessEnrichPlacementResultMessagePositionBuyerNotExists() {
    var msg = new EnrichPlacementResultMessage();
    msg.setPlacementPid(POSITION_ID.toString());
    msg.setCompanyPid(COMPANY_ID.toString());
    msg.setSitePid(SITE_ID.toString());
    msg.setSectionPid(SECTION_ID);

    Map<String, Object> headers =
        Map.of(
            "headerKey1",
            "headervalue1",
            MessageHeaders.CORRELATION_ID_HEADER,
            UUID.randomUUID().toString());

    when(positionBuyerRepository.findByPositionPid(POSITION_ID)).thenReturn(Optional.empty());
    verifyNoMoreInteractions(positionBuyerRepository);
    when(positionRepository.findById(POSITION_ID)).thenReturn(Optional.of(new Position()));

    subject.processMessage(msg, headers);

    ArgumentCaptor<Position> someArgumentCaptor = ArgumentCaptor.forClass(Position.class);
    verify(positionRepository).save(someArgumentCaptor.capture());

    Position savedPosition = someArgumentCaptor.getValue();
    PositionBuyer savedPositionBuyer = savedPosition.getPositionBuyer();
    assertEquals(SECTION_ID, savedPositionBuyer.getBuyerPositionId());
    assertEquals(GEMINI_COMPANY_ID, savedPositionBuyer.getCompanyPid());
    assertEquals(POSITION_ID, savedPositionBuyer.getPositionPid());
    assertEquals(1, savedPositionBuyer.getVersion().intValue());
  }

  @Test
  void testProcessEnrichPlacementResultMessagePositionBuyerNotExistsAndPositionNotExists() {
    var msg = new EnrichPlacementResultMessage();
    msg.setPlacementPid(POSITION_ID.toString());
    msg.setCompanyPid(COMPANY_ID.toString());
    msg.setSitePid(SITE_ID.toString());
    msg.setSectionPid(SECTION_ID);

    Map<String, Object> headers =
        Map.of(
            "headerKey1",
            "headervalue1",
            MessageHeaders.CORRELATION_ID_HEADER,
            UUID.randomUUID().toString());

    when(positionBuyerRepository.findByPositionPid(POSITION_ID)).thenReturn(Optional.empty());
    verifyNoMoreInteractions(positionBuyerRepository);

    when(positionRepository.findById(POSITION_ID)).thenReturn(Optional.empty());
    verifyNoMoreInteractions(positionRepository);

    subject.processMessage(msg, headers);
  }

  @Test
  void testProcessEnrichPlacementResultMessageNoOp() {
    ReflectionTestUtils.setField(subject, "enableSync", false);

    EnrichPlacementResultMessage msg = new EnrichPlacementResultMessage();
    msg.setSectionPid("12");
    msg.setPlacementPid("10");

    Map<String, Object> headers = Map.of("headerKey1", "headervalue1");
    subject.processMessage(msg, headers);

    verifyNoInteractions(positionBuyerRepository);
    verifyNoInteractions(positionRepository);
  }
}
