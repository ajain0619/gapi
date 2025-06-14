package com.nexage.app.mapper.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.filter.FilterListType;
import com.nexage.app.dto.filter.FilterListTypeDTO;
import com.nexage.app.mapper.FilterListTypeMapper;
import org.junit.jupiter.api.Test;

class FilterListTypeMapperTest {

  private FilterListTypeMapper filterListTypeMapper = FilterListTypeMapper.MAPPER;

  @Test
  void shouldMapFilterListTypeDTOtoEntity() {
    assertEquals(FilterListType.DOMAIN, filterListTypeMapper.map(FilterListTypeDTO.DOMAIN));
  }

  @Test
  void shouldMapFilterListTypeEntityToDTO() {
    assertEquals(FilterListTypeDTO.DOMAIN, filterListTypeMapper.map(FilterListType.DOMAIN));
  }
}
