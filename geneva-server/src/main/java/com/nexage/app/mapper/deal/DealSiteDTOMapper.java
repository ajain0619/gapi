package com.nexage.app.mapper.deal;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.app.dto.deals.DealSiteDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DealSiteDTOMapper {

  DealSiteDTOMapper MAPPER = Mappers.getMapper(DealSiteDTOMapper.class);

  default DealSite map(DealSiteDTO dealSiteDTO, DirectDeal deal) {
    var siteView = new DealSite();
    siteView.setDeal(deal);
    siteView.setPid(dealSiteDTO.getPid());
    siteView.setSitePid(dealSiteDTO.getSitePid());
    return siteView;
  }

  default DealSiteDTO map(DealSite dealSite, List<DealPosition> dealPositions) {
    var dealSiteDto = new DealSiteDTO();
    dealSiteDto.setPid(dealSite.getPid());
    dealSiteDto.setSitePid(dealSite.getSitePid());
    dealSiteDto.setSiteName(dealSite.getSiteView().getName());
    if (dealPositions != null) {
      dealSiteDto.setPlacements(
          dealPositions.stream()
              .map(DealPlacementDTOMapper.MAPPER::map)
              .collect(Collectors.toList()));
    }
    return dealSiteDto;
  }
}
