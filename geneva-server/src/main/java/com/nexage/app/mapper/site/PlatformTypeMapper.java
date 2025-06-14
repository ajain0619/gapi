package com.nexage.app.mapper.site;

import com.nexage.admin.core.enums.site.Platform;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlatformTypeMapper {

  PlatformTypeMapper MAPPER = Mappers.getMapper(PlatformTypeMapper.class);

  Platform map(PublisherSiteDTO.Platform platform);

  PublisherSiteDTO.Platform map(Platform platform);
}
