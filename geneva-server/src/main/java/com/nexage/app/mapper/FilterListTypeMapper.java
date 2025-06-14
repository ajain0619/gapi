package com.nexage.app.mapper;

import com.nexage.admin.core.model.filter.FilterListType;
import com.nexage.app.dto.filter.FilterListTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilterListTypeMapper {
  FilterListTypeMapper MAPPER = Mappers.getMapper(FilterListTypeMapper.class);

  FilterListType map(FilterListTypeDTO filterListTypeDTO);

  FilterListTypeDTO map(FilterListType filterListType);
}
