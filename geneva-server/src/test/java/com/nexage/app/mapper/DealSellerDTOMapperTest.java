package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.app.dto.deals.DealSellerDTO;
import com.nexage.app.mapper.deal.DealSellerDTOMapper;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DealSellerDTOMapperTest {

  @Test
  void shouldMapToEntity() {
    var sellerDto = new DealSellerDTO();
    sellerDto.setSellerName("some seller");
    sellerDto.setSellerPid(1L);

    var deal = new DirectDeal();
    deal.setPid(2L);
    var out = DealSellerDTOMapper.MAPPER.map(sellerDto, deal);
    assertEquals(sellerDto.getSellerPid(), out.getPubPid());
    assertEquals(deal.getPid(), out.getDeal().getPid());
  }

  @Test
  void shouldMapToDto() {
    long sellerPid = 1L;
    long dealPublisherPid = 111L;
    long sellerSeatPid = 10L;
    long sitePid = 2L;
    var dealPublisher = new DealPublisher();
    dealPublisher.setPubPid(sellerPid);
    dealPublisher.setPid(dealPublisherPid);

    var companyView =
        new CompanyView(sellerPid, "company", CompanyType.SELLER, true, sellerSeatPid);
    dealPublisher.setCompanyView(companyView);

    var dealSite = new DealSite();
    dealSite.setSitePid(sitePid);
    var siteView = new SiteView();
    siteView.setName("site name");
    dealSite.setSiteView(siteView);
    var siteMap = Map.of(sellerPid, List.of(dealSite));
    Map<Long, List<DealPosition>> positionMap = Map.of();

    var out = DealSellerDTOMapper.MAPPER.map(dealPublisher, siteMap, positionMap);
    assertEquals(dealPublisher.getPubPid(), out.getSellerPid());
    assertEquals(dealPublisher.getPid(), out.getPid());
    assertEquals(companyView.getName(), out.getSellerName());
    assertEquals(companyView.getSellerSeatPid(), out.getSellerSeatPid());
    assertEquals(1, out.getSites().size());
  }
}
