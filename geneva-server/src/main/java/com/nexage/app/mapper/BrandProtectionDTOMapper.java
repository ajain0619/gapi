package com.nexage.app.mapper;

import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.app.dto.brand.protection.BrandProtectionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandProtectionDTOMapper {
  BrandProtectionDTOMapper MAPPER = Mappers.getMapper(BrandProtectionDTOMapper.class);

  @Mapping(target = "categoryId", source = "brandProtectionTag.category.pid")
  @Mapping(target = "crsTags", source = "brandProtectionTag.crsTagMappings")
  @Mapping(target = "parentTagPid", source = "brandProtectionTag.parentTag.pid")
  BrandProtectionDTO map(BrandProtectionTag brandProtectionTag);
}
