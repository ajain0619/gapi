package com.nexage.app.mapper;

import com.nexage.admin.core.model.RevenueGroup;
import com.nexage.app.dto.RevenueGroupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RevenueGroupDTOMapper {

  RevenueGroupDTOMapper MAPPER = Mappers.getMapper(RevenueGroupDTOMapper.class);

  RevenueGroupDTO map(RevenueGroup input);
}
