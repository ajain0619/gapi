package com.nexage.app.mapper.rule;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RuleDTOMapper {

  RuleDTOMapper MAPPER = Mappers.getMapper(RuleDTOMapper.class);
}
