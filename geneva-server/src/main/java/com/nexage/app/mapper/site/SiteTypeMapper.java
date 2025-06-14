package com.nexage.app.mapper.site;

import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SiteTypeMapper {

  SiteTypeMapper MAPPER = Mappers.getMapper(SiteTypeMapper.class);

  Type map(SiteType siteType);

  SiteType map(Type siteType);
}
