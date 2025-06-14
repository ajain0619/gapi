package com.nexage.app.mapper;

import com.nexage.admin.core.model.ExchangeRate;
import com.nexage.app.dto.ExchangeRateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExchangeRateDTOMapper {

  ExchangeRateDTOMapper MAPPER = Mappers.getMapper(ExchangeRateDTOMapper.class);

  @Mapping(source = "id.currency", target = "currency")
  @Mapping(source = "id.checkDate", target = "checkDate")
  ExchangeRateDTO map(ExchangeRate exchangeRate);
}
