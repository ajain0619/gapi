package com.nexage.app.mapper;

import com.nexage.admin.core.model.filter.FilterListDomain;
import com.nexage.app.dto.filter.FilterListDomainDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilterListDomainMapper {

  FilterListDomainMapper INSTANCE = Mappers.getMapper(FilterListDomainMapper.class);

  @Mapping(source = "domain.domain", target = "domain")
  FilterListDomainDTO map(FilterListDomain filterListDomain);

  @Mapping(source = "domain", target = "domain.domain")
  FilterListDomain map(FilterListDomainDTO filterListDomainDTO);
}
