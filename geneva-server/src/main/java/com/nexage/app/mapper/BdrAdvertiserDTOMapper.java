package com.nexage.app.mapper;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.app.dto.BdrAdvertiserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/** Defines bean mappings between {@link BdrAdvertiserDTO} and {@link BDRAdvertiser}. */
@Mapper
public interface BdrAdvertiserDTOMapper {

  BdrAdvertiserDTOMapper MAPPER = Mappers.getMapper(BdrAdvertiserDTOMapper.class);

  BDRAdvertiser map(BdrAdvertiserDTO source);
}
