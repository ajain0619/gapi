package com.nexage.app.mapper;

import com.nexage.admin.core.model.SellerSeat;
import com.nexage.app.dto.SellerSeatDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CompanyViewDTOMapper.class})
public interface SellerSeatDTOMapper {
  SellerSeatDTOMapper MAPPER = Mappers.getMapper(SellerSeatDTOMapper.class);

  SellerSeatDTO map(SellerSeat sellerSeat);

  @Mapping(target = "sellers", ignore = true)
  SellerSeat map(SellerSeatDTO sellerSeat);

  @Mapping(target = "sellers", ignore = true)
  void updateEntityFromDTO(SellerSeatDTO sellerSeatDTO, @MappingTarget SellerSeat sellerSeat);
}
