package com.nexage.app.mapper.deal;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.app.dto.deals.DealSellerDTO;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DealSellerDTOMapper {

  DealSellerDTOMapper MAPPER = Mappers.getMapper(DealSellerDTOMapper.class);

  default DealPublisher map(DealSellerDTO dealSellerDTO, DirectDeal deal) {
    var publisher = new DealPublisher();
    publisher.setDeal(deal);
    publisher.setPid(dealSellerDTO.getPid());
    publisher.setPubPid(dealSellerDTO.getSellerPid());
    return publisher;
  }

  default DealSellerDTO map(
      DealPublisher dealPublisher,
      Map<Long, List<DealSite>> dealSiteMap,
      Map<Long, List<DealPosition>> dealPositionMap) {
    var dealSeller = new DealSellerDTO();
    dealSeller.setPid(dealPublisher.getPid());
    dealSeller.setSellerPid(dealPublisher.getPubPid());
    dealSeller.setSellerName(dealPublisher.getCompanyView().getName());
    dealSeller.setSellerSeatPid(dealPublisher.getCompanyView().getSellerSeatPid());
    if (dealSiteMap.containsKey(dealSeller.getSellerPid())) {
      dealSeller.setSites(
          dealSiteMap.get(dealSeller.getSellerPid()).stream()
              .map(
                  dealSite ->
                      DealSiteDTOMapper.MAPPER.map(
                          dealSite,
                          dealPositionMap.containsKey(dealSite.getSitePid())
                              ? dealPositionMap.get(dealSite.getSitePid())
                              : List.of()))
              .collect(Collectors.toList()));
    }
    return dealSeller;
  }
}
