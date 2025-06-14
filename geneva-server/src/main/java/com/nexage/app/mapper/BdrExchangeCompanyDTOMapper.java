package com.nexage.app.mapper;

import com.nexage.admin.core.bidder.model.BdrExchangeCompany;
import com.nexage.app.dto.bdr.BdrExchangeCompanyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BdrExchangeCompanyDTOMapper {
  BdrExchangeCompanyDTOMapper MAPPER = Mappers.getMapper(BdrExchangeCompanyDTOMapper.class);

  BdrExchangeCompany map(BdrExchangeCompanyDTO source);

  BdrExchangeCompanyDTO map(BdrExchangeCompany source);
}
