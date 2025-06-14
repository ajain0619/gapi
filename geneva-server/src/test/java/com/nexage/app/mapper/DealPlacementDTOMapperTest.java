package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.deals.DealPlacementDTO;
import com.nexage.app.mapper.deal.DealPlacementDTOMapper;
import org.junit.jupiter.api.Test;

class DealPlacementDTOMapperTest {

  @Test
  void shouldMapToEntity() {
    var dealPlacementDTO = new DealPlacementDTO();
    dealPlacementDTO.setPlacementPid(1L);
    dealPlacementDTO.setPid(123L);

    var deal = new DirectDeal();
    deal.setPid(2L);

    var positionView = new PositionView();
    positionView.setPid(3L);
    var out = DealPlacementDTOMapper.MAPPER.map(dealPlacementDTO, deal, positionView);
    assertEquals(dealPlacementDTO.getPlacementPid(), out.getPositionPid());
    assertEquals(dealPlacementDTO.getPid(), out.getPid());
    assertEquals(deal.getPid(), out.getDeal().getPid());
    assertEquals(positionView.getPid(), out.getPositionView().getPid());
  }

  @Test
  void shouldMapToDto() {
    var dealPosition = new DealPosition();
    dealPosition.setPositionPid(1L);
    dealPosition.setPid(111L);

    var positionView = new PositionView();
    positionView.setName("name");
    positionView.setMemo("memo");
    dealPosition.setPositionView(positionView);

    var out = DealPlacementDTOMapper.MAPPER.map(dealPosition);
    assertEquals(dealPosition.getPositionPid(), out.getPlacementPid());
    assertEquals(dealPosition.getPid(), out.getPid());
    assertEquals(positionView.getName(), out.getPlacementName());
    assertEquals(positionView.getMemo(), out.getPlacementMemo());
  }
}
