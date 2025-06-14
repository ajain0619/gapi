package com.nexage.app.queue.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.PositionBuyer;
import com.nexage.admin.core.model.Site;
import com.nexage.app.queue.model.EnrichPlacementCommandMessage;
import com.nexage.app.queue.model.EnrichPlacementResultMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnrichPlacementMessageMapperTest {

  private static final Long POSITION_PID = 10L;
  private static final Long COMPANY_PID = 11L;
  private static final Long SITE_PID = 12L;
  private static final String POSITION_NAME = "some name";
  private static final String SECTION_ID = "123";

  @Mock Position position;

  @Mock Site site;

  @Test
  void testMapFromPositionToEnrichPlacementCommandMessage() {
    when(position.getPid()).thenReturn(POSITION_PID);
    when(position.getMemo()).thenReturn(POSITION_NAME);
    when(position.getStatus()).thenReturn(Status.ACTIVE);
    when(position.getSite()).thenReturn(site);
    when(site.getPid()).thenReturn(SITE_PID);

    EnrichPlacementCommandMessage actual = EnrichPlacementMessageMapper.MAPPER.map(position);

    assertEquals(POSITION_PID.toString(), actual.getPlacementPid());
    assertEquals(SITE_PID.toString(), actual.getSitePid());
    assertEquals(Status.ACTIVE.toString(), actual.getStatus());
    assertEquals(POSITION_NAME, actual.getName());
  }

  @Test
  void testMapFromEnrichPlacementCommandMessageToPositionBuyer() {
    EnrichPlacementResultMessage msg = new EnrichPlacementResultMessage();
    msg.setPlacementPid(POSITION_PID.toString());
    msg.setCompanyPid(COMPANY_PID.toString());
    msg.setSectionPid(SECTION_ID);

    PositionBuyer actual = EnrichPlacementMessageMapper.MAPPER.map(msg);

    assertEquals(POSITION_PID, actual.getPositionPid());
    assertEquals(COMPANY_PID, actual.getCompanyPid());
    assertEquals(SECTION_ID, actual.getBuyerPositionId());
    assertEquals(1, actual.getVersion().intValue());
  }
}
