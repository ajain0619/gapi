package com.nexage.app.mapper;

import com.nexage.admin.core.model.CrsTagMapping;
import com.nexage.app.dto.CrsTagMappingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CrsTagDTOMapping {
  CrsTagDTOMapping MAPPER = Mappers.getMapper(CrsTagDTOMapping.class);

  @Mapping(target = "brandProtectionTagPid", source = "crsTagMapping.tag.pid")
  CrsTagMappingDTO map(CrsTagMapping crsTagMapping);
}
