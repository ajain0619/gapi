package com.nexage.app.mapper.site;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.mapper.PlacementDoohDTOMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {PlacementDoohDTOMapper.class})
public interface PlacementDTOMapper {

  PlacementDTOMapper MAPPER = Mappers.getMapper(PlacementDTOMapper.class);

  @Mapping(source = "isInterstitial", target = "interstitial")
  @Mapping(
      nullValuePropertyMappingStrategy = IGNORE,
      source = "site.status",
      target = "site.status")
  @Mapping(nullValuePropertyMappingStrategy = IGNORE, source = "placementDooh", target = "dooh")
  PlacementDTO map(Position source);

  @InheritInverseConfiguration
  @Mapping(
      nullValuePropertyMappingStrategy = IGNORE,
      source = "dooh",
      target = "placementDooh",
      dependsOn = "pid")
  Position map(PlacementDTO placementDTO);

  @Mapping(nullValuePropertyMappingStrategy = IGNORE, source = "pid", target = "pid")
  PlacementDTO map(PositionView source);

  @AfterMapping
  default void after(@MappingTarget Position position) {
    position.setIsDefault(false);
  }
}
