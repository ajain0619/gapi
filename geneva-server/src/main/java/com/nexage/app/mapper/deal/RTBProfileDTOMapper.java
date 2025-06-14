package com.nexage.app.mapper.deal;

import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import com.nexage.app.dto.deal.RTBProfileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RTBProfileDTOMapper extends DirectDealExtensionDTOMapper {

  RTBProfileDTOMapper MAPPER = Mappers.getMapper(RTBProfileDTOMapper.class);

  @Mapping(target = "rtbProfilePid", source = "pid")
  @Mapping(target = "lowFloor", source = "lowReserve")
  @Mapping(target = "pubId", source = "pubPid")
  @Mapping(target = "siteId", source = "sitePid")
  RTBProfileDTO map(DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas);
}
