package com.nexage.app.mapper.site;

import com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation;
import com.nexage.app.dto.seller.PlacementSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlacementSummaryDTOMapper {
  PlacementSummaryDTOMapper MAPPER = Mappers.getMapper(PlacementSummaryDTOMapper.class);

  PlacementSummaryDTO map(PositionMetricsAggregation source);
}
