package com.nexage.app.mapper;

import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.seller.SellerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SellerDTOMapper {

  SellerDTOMapper MAPPER = Mappers.getMapper(SellerDTOMapper.class);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "pid", target = "pid")
  @Mapping(source = "sellerAttributes.revenueGroupPid", target = "revenueGroupPid")
  SellerDTO map(Company source);
}
