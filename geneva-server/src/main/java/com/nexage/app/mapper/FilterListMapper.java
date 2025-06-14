package com.nexage.app.mapper;

import com.nexage.admin.core.model.filter.FilterList;
import com.nexage.app.dto.filter.FilterListDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilterListMapper {

  FilterListMapper MAPPER = Mappers.getMapper(FilterListMapper.class);

  @Mappings(
      value = {
        @Mapping(source = "buyerId", target = "companyId"),
      })
  FilterList map(FilterListDTO filterListDTO);

  @Mappings(
      value = {
        @Mapping(source = "companyId", target = "buyerId"),
      })
  FilterListDTO map(FilterList filterList);

  @AfterMapping
  default void after(
      FilterList filterList, @MappingTarget FilterListDTO.FilterListDTOBuilder filterListDTO) {
    int valid;
    int total = filterList.getTotal() != null ? filterList.getTotal() : 0;
    int duplicate = filterList.getDuplicate() != null ? filterList.getDuplicate() : 0;
    int error = filterList.getError() != null ? filterList.getError() : 0;
    int invalid = filterList.getInvalid() != null ? filterList.getInvalid() : 0;
    valid = total - (duplicate + error + invalid);
    filterListDTO.valid(valid < 0 ? 0 : valid);
  }
}
