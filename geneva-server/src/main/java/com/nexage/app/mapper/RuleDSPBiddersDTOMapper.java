package com.nexage.app.mapper;

import com.nexage.admin.core.model.RuleDSPBiddersView;
import com.nexage.app.dto.RuleDSPBiddersDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RuleDSPBiddersDTOMapper {

  RuleDSPBiddersDTOMapper MAPPER = Mappers.getMapper(RuleDSPBiddersDTOMapper.class);

  RuleDSPBiddersDTO map(RuleDSPBiddersView source);
}
