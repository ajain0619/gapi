package com.nexage.app.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.SET_TO_DEFAULT;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.app.dto.user.CompanyViewDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompanyViewDTOMapper {

  CompanyViewDTOMapper MAPPER = Mappers.getMapper(CompanyViewDTOMapper.class);

  @Mapping(
      source = "sellerAttributes.adStrictApproval",
      target = "adStrictApproval",
      nullValuePropertyMappingStrategy = SET_TO_DEFAULT)
  CompanyViewDTO map(Company company);

  CompanyViewDTO map(CompanyView company);
}
