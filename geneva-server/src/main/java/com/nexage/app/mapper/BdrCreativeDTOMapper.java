package com.nexage.app.mapper;

import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.app.dto.BdrCreativeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/** Defines bean mappings between {@link BdrCreativeDTO} and {@link BdrCreative}. */
@Mapper(uses = {BdrAdvertiserDTOMapper.class})
public interface BdrCreativeDTOMapper {

  BdrCreativeDTOMapper MAPPER = Mappers.getMapper(BdrCreativeDTOMapper.class);

  BdrCreative map(BdrCreativeDTO source);
}
