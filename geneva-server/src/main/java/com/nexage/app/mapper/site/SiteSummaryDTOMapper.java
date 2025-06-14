package com.nexage.app.mapper.site;

import com.nexage.admin.core.sparta.jpa.model.SiteMetricsAggregation;
import com.nexage.app.dto.seller.SiteSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SiteSummaryDTOMapper {
  SiteSummaryDTOMapper MAPPER = Mappers.getMapper(SiteSummaryDTOMapper.class);

  @Mapping(source = "pid", target = "PId")
  SiteSummaryDTO map(SiteMetricsAggregation source);
}
