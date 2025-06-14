package com.nexage.app.mapper;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.mapper.site.NativePlacementExtensionDTOMapper;
import com.nexage.app.services.NativePlacementHbPartnerService;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = NativePlacementExtensionDTOMapper.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NativePlacementDTOMapper {

  @Mapping(source = "sitePid", target = "sitePid")
  @Mapping(target = "site", ignore = true)
  @Mapping(
      source = "nativeConfig",
      target = "nativePlacementExtension",
      qualifiedByName = "convertToNativePlacementExtensionDto")
  @Mapping(source = "positionBuyer.version", target = "placementBuyer.version")
  @Mapping(source = "positionBuyer.buyerPositionId", target = "placementBuyer.sectionId")
  @Mapping(source = "positionBuyer.pid", target = "placementBuyer.pid")
  @Mapping(target = "placementBuyer", ignore = true)
  NativePlacementDTO map(
      Position position, @Context NativePlacementHbPartnerService nativePlacementHbPartnerService);

  default Position map(
      NativePlacementDTO source,
      @Context NativePlacementHbPartnerService nativePlacementHbPartnerService,
      @Context Site site) {
    return map(source, new Position(), nativePlacementHbPartnerService, site);
  }

  @Mapping(target = "isDefault", constant = "false")
  @Mapping(target = "isInterstitial", constant = "false")
  @Mapping(target = "hbPartnerPosition", ignore = true)
  @Mapping(target = "site", ignore = true)
  @Mapping(target = "tiers", ignore = true)
  @Mapping(
      source = "nativePlacementExtension",
      target = "nativeConfig",
      qualifiedByName = "convertToPosition")
  @Mapping(source = "placementBuyer.version", target = "positionBuyer.version")
  @Mapping(source = "placementBuyer.sectionId", target = "positionBuyer.buyerPositionId")
  @Mapping(source = "placementBuyer.pid", target = "positionBuyer.pid")
  @Mapping(source = "pid", target = "positionBuyer.positionPid")
  Position map(
      NativePlacementDTO source,
      @MappingTarget Position position,
      @Context NativePlacementHbPartnerService nativePlacementHbPartnerService,
      @Context Site site);

  @AfterMapping
  default void handlePojoToPositionBuyer(@MappingTarget Position targetPosition) {
    if (targetPosition.getPositionBuyer() != null) {
      if (StringUtils.isEmpty(targetPosition.getPositionBuyer().getBuyerPositionId())) {
        targetPosition.setPositionBuyer(null);
      } else {
        targetPosition.getPositionBuyer().setPosition(targetPosition);
      }
    }
  }

  @AfterMapping
  default void mapHbPartnerAssignment(
      @MappingTarget Position position,
      NativePlacementDTO nativePlacementDTO,
      @Context NativePlacementHbPartnerService nativePlacementHbPartnerService,
      @Context Site site) {
    nativePlacementHbPartnerService.handleHbPartnersAssignmentMapping(
        nativePlacementDTO, position, site);
  }

  @AfterMapping
  default void mapHbPartnerPosition(
      @MappingTarget NativePlacementDTO nativePlacementDTO,
      Position position,
      @Context NativePlacementHbPartnerService nativePlacementHbPartnerService) {
    nativePlacementHbPartnerService.handleHbPartnerPositionMapping(nativePlacementDTO, position);
  }
}
