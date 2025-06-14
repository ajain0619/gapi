package com.nexage.app.mapper;

import com.nexage.admin.core.model.PlacementDooh;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlacementDoohDTOMapper {
  PlacementDoohDTOMapper MAPPER = Mappers.getMapper(PlacementDoohDTOMapper.class);

  @Mapping(defaultValue = "1", target = "defaultImpressionMultiplier")
  @Mapping(defaultValue = "0", target = "version")
  PlacementDoohDTO map(PlacementDooh placementDooh);

  @Mapping(defaultValue = "1", target = "defaultImpressionMultiplier")
  PlacementDooh map(PlacementDoohDTO placementDoohDTO);
}
