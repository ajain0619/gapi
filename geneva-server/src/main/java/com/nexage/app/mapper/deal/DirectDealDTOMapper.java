package com.nexage.app.mapper.deal;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.app.dto.DirectDealDTO;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DirectDealDTOMapper extends DirectDealExtensionDTOMapper {

  @BeforeMapping
  default void convertPlacementFormulaBeforeMapping(
      @MappingTarget DirectDealDTO.DirectDealDTOBuilder<?, ?> directDealDTO,
      DirectDeal directDeal) {
    if (directDeal.getPlacementFormula() != null) {
      directDealDTO.placementFormula(
          DirectDealExtensionDTOMapper.placementFormulaMake(directDeal.getPlacementFormula()));
      directDealDTO.autoUpdate(directDeal.getAutoUpdate());
    }
  }

  DirectDealDTOMapper MAPPER = Mappers.getMapper(DirectDealDTOMapper.class);

  @Mappings({
    @Mapping(source = "publishers", target = "sellers", qualifiedByName = "convertPublishers"),
    @Mapping(source = "rules", target = "rules", qualifiedByName = "convertDealRules"),
    @Mapping(
        source = "auctionType",
        target = "auctionType",
        qualifiedByName = "convertAuctionType"),
    @Mapping(source = "dealCategory", target = "dealCategory"),
    @Mapping(source = "positions", target = "positions", qualifiedByName = "convertDealPositions"),
    @Mapping(source = "sites", target = "sites", qualifiedByName = "convertSites"),
    @Mapping(
        source = "profiles",
        target = "profiles",
        qualifiedByName = "filterAndConvertProfilesToDtos"),
    @Mapping(source = "placementFormula", target = "placementFormula", ignore = true),
    @Mapping(source = "autoUpdate", target = "autoUpdate", ignore = true),
    @Mapping(source = "bidders", target = "bidders", qualifiedByName = "convertBidders"),
    @Mapping(source = "dealTargets", target = "targets", qualifiedByName = "convertTargets")
  })
  DirectDealDTO map(DirectDeal directDeal);
}
