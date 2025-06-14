package com.nexage.app.mapper;

import com.nexage.admin.core.model.filter.FilterListAppBundle;
import com.nexage.app.dto.filter.FilterListAppBundleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilterListAppBundleMapper {

  FilterListAppBundleMapper MAPPER = Mappers.getMapper(FilterListAppBundleMapper.class);

  @Mapping(source = "app.appBundleId", target = "app")
  FilterListAppBundleDTO map(FilterListAppBundle filterListAppBundle);

  @Mapping(source = "app", target = "app.appBundleId")
  FilterListAppBundle map(FilterListAppBundleDTO filterListAppBundleDTO);
}
