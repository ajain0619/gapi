package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.deals.DealSiteDTO;
import com.nexage.app.mapper.deal.DealSiteDTOMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class DealSiteDTOMapperTest {

  @Test
  void shouldMapToEntity() {
    var siteDto = new DealSiteDTO();
    siteDto.setSitePid(1L);

    var deal = new DirectDeal();
    deal.setPid(2L);
    var out = DealSiteDTOMapper.MAPPER.map(siteDto, deal);
    assertEquals(siteDto.getSitePid(), out.getSitePid());
    assertEquals(deal.getPid(), out.getDeal().getPid());
  }

  @Test
  void shouldMapToDto() {
    var dealSite = new DealSite();
    dealSite.setSitePid(1L);
    dealSite.setPid(111L);
    var siteView = new SiteView();
    siteView.setName("site name");
    dealSite.setSiteView(siteView);

    var dealPosition = new DealPosition();
    dealPosition.setPositionPid(2L);
    var positionView = new PositionView();
    positionView.setName("name");
    positionView.setMemo("memo");
    dealPosition.setPositionView(positionView);
    var positions = List.of(dealPosition);

    var out = DealSiteDTOMapper.MAPPER.map(dealSite, positions);
    assertEquals(dealSite.getSitePid(), out.getSitePid());
    assertEquals(dealSite.getPid(), out.getPid());
    assertEquals(siteView.getName(), out.getSiteName());
    assertEquals(positions.size(), out.getPlacements().size());
  }
}
