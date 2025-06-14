package com.nexage.app.mapper.deal;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.app.dto.DirectDealDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DirectDealDTOMapperWithoutSuppliersAndBidders
    extends DirectDealExtensionDTOMapper {

  DirectDealDTOMapperWithoutSuppliersAndBidders MAPPER =
      Mappers.getMapper(DirectDealDTOMapperWithoutSuppliersAndBidders.class);

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
    @Mapping(source = "profiles", target = "profiles", ignore = true),
    @Mapping(source = "placementFormula", target = "placementFormula", ignore = true),
    @Mapping(source = "autoUpdate", target = "autoUpdate", ignore = true),
    @Mapping(source = "bidders", target = "bidders", qualifiedByName = "convertBidders"),
    @Mapping(source = "dealTargets", target = "targets", qualifiedByName = "convertTargets"),
  })
  DirectDealDTO map(DirectDeal directDeal);
}
