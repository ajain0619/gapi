package com.nexage.app.mapper;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.app.dto.BidderConfigDTOView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BidderConfigDTOViewMapper {

  BidderConfigDTOViewMapper MAPPER = Mappers.getMapper(BidderConfigDTOViewMapper.class);

  BidderConfigDTOView map(BidderConfig source);
}
