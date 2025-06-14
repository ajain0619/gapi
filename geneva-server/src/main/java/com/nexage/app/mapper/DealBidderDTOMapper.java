package com.nexage.app.mapper;

import com.nexage.admin.core.model.DealBidderConfigView;
import com.nexage.app.dto.deals.DealBidderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DealBidderDTOMapper {

  DealBidderDTOMapper MAPPER = Mappers.getMapper(DealBidderDTOMapper.class);

  DealBidderDTO map(DealBidderConfigView source);
}
