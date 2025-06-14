package com.nexage.app.mapper.site;

import com.nexage.admin.core.sparta.jpa.model.PlacementVideoCompanion;
import com.nexage.app.dto.seller.PlacementVideoCompanionDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlacementVideoCompanionDTOMapper {

  PlacementVideoCompanionDTOMapper MAPPER =
      Mappers.getMapper(PlacementVideoCompanionDTOMapper.class);

  PlacementVideoCompanionDTO map(PlacementVideoCompanion source);

  @InheritInverseConfiguration
  PlacementVideoCompanion map(PlacementVideoCompanionDTO placementVideoCompanionDTO);
}
