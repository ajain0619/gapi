package com.nexage.app.mapper.site;

import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.SiteView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SiteViewMapper {

  SiteViewMapper MAPPER = Mappers.getMapper(SiteViewMapper.class);

  Site map(SiteView siteView);
}
