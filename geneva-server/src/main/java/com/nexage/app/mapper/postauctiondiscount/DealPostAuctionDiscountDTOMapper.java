package com.nexage.app.mapper.postauctiondiscount;

import com.nexage.admin.core.model.DirectDealView;
import com.nexage.admin.core.model.postauctiondiscount.DealPostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.app.dto.postauctiondiscount.DirectDealViewDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DealPostAuctionDiscountDTOMapper {

  DealPostAuctionDiscountDTOMapper MAPPER =
      Mappers.getMapper(DealPostAuctionDiscountDTOMapper.class);

  @Mapping(target = "pid", source = "deal.pid")
  @Mapping(target = "description", source = "deal.description")
  @Mapping(target = "id", source = "deal.id")
  DirectDealViewDTO map(DealPostAuctionDiscount deal);

  DirectDealView map(DirectDealViewDTO deal);

  DirectDealViewDTO map(DirectDealView dealView);

  default List<DealPostAuctionDiscount> map(
      List<DirectDealViewDTO> deals, PostAuctionDiscount discount) {

    return deals.stream()
        .map(dealViewDTO -> getDealFromDiscountOrElseCreateFromDto(dealViewDTO, discount))
        .toList();
  }

  private DealPostAuctionDiscount getDealFromDiscountOrElseCreateFromDto(
      DirectDealViewDTO dealViewDTO, PostAuctionDiscount discount) {
    return discount.getDeals().stream()
        .filter(deal -> dealViewDTO.getPid().equals(deal.getDeal().getPid()))
        .findAny()
        .orElseGet(() -> createDealFromDto(dealViewDTO, discount));
  }

  private DealPostAuctionDiscount createDealFromDto(
      DirectDealViewDTO dealViewDTO, PostAuctionDiscount discount) {
    var dealPad = new DealPostAuctionDiscount();
    dealPad.setPostAuctionDiscount(discount);
    dealPad.setDeal(map(dealViewDTO));
    return dealPad;
  }
}
