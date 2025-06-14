package com.nexage.app.mapper;

import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.app.dto.brand.protection.BrandProtectionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandProtectionDTOParentsOnlyMapper {
  BrandProtectionDTOParentsOnlyMapper MAPPER =
      Mappers.getMapper(BrandProtectionDTOParentsOnlyMapper.class);

  @Mapping(target = "categoryId", source = "brandProtectionTag.category.pid")
  @Mapping(target = "crsTags", source = "brandProtectionTag.parentTag.crsTagMappings")
  @Mapping(target = "pid", source = "brandProtectionTag.parentTag.pid")
  @Mapping(target = "name", source = "brandProtectionTag.parentTag.name")
  @Mapping(target = "rtbId", source = "brandProtectionTag.parentTag.rtbId")
  BrandProtectionDTO map(BrandProtectionTag brandProtectionTag);
}
